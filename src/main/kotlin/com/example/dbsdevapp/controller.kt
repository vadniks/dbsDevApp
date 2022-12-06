package com.example.dbsdevapp

import com.example.dbsdevapp.entity.*
import com.example.dbsdevapp.repo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

private typealias VoidResponse = ResponseEntity<Void>
private val responseOk = VoidResponse(HttpStatus.OK)
private val responseBadRequest = VoidResponse(HttpStatus.BAD_REQUEST)
private const val AUTH_CREDENTIALS = "Auth-credentials"
typealias Json = Map<String, Any?>
private const val WHICH = "/{which}"
inline fun <reified T : Any?> Json.getTyped(key: String) = get(key) as T?

@RestController
class Controller(
    private val componentRepo: ComponentRepo,
    private val clientRepo: ClientRepo,
    private val employeeInfoRepo: EmployeeInfoRepo,
    private val employeesRepo: EmployeesRepo,
    private val orderRepo: OrderRepo
) {

    // curl 'localhost:8080/component' -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"componentId":null,"name":"aa","type":1,"description":"bb","cost":10,"image":null,"count":1}'
    @PostMapping(WHICH)
    fun insert(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return if (when (which) {
            COMPONENT -> componentRepo.insert(json.component)
            CLIENT -> clientRepo.insert(json.client)
//            EMPLOYEE_INFO -> employeeInfoRepo.insert(json.employeeInfo)
//            MANAGER -> employeesRepo.insert(json.manager)
//            DELIVERY_WORKER -> employeesRepo.insert(json.deliveryWorker)
//            ADMINISTRATOR -> employeesRepo.insert(json.administrator)
//            ORDER -> orderRepo.insert(json.order)
            else -> false
        }) responseOk else responseBadRequest
    }

    @Transactional
    @PostMapping("/newEmployee")
    fun newEmployee(
        @RequestParam which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest

        val employeeInfo = try { json.employeeInfo } 
        catch (_: Exception) { null } ?: return responseBadRequest

        if (!employeeInfoRepo.insert(employeeInfo))
            return responseBadRequest
        val employeeId = employeeInfoRepo.get(employeeInfo.email)

        return if (employeesRepo.insert(when (which) {
            MANAGER -> Manager(employeeId)
            DELIVERY_WORKER -> DeliveryWorker(employeeId)
            ADMINISTRATOR -> Administrator(employeeId)
            else -> return responseBadRequest
        })) responseOk else responseBadRequest
    }

    // curl 'localhost:8080/component?id=1' -H 'Auth-credentials: admin:admin'
    @ResponseBody
    @GetMapping(WHICH)
    fun get(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ): Json? {
        if (credentials != "admin:admin") return null
        return when (which) {
            COMPONENT -> componentRepo.get(id)
            CLIENT -> clientRepo.get(id)
//            EMPLOYEE_INFO -> employeeInfoRepo.get(id)
//            MANAGER, DELIVERY_WORKER, ADMINISTRATOR -> employeesRepo.get(id, which)
//            ORDER -> orderRepo.get(id)
            else -> null
        }?.json
    }

    // curl 'localhost:8080/all/component' -H 'Auth-credentials: admin:admin'
    @ResponseBody
    @GetMapping("/all$WHICH")
    fun getAll(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ): List<Json> {
        if (credentials != "admin:admin") return emptyList()
        return when (which) {
            COMPONENT -> componentRepo.get()
            CLIENT -> clientRepo.get()
            EMPLOYEE_INFO -> employeeInfoRepo.get()
            MANAGER, DELIVERY_WORKER, ADMINISTRATOR -> employeesRepo.get(which)
            ORDER -> orderRepo.get()
            else -> emptyList()
        }.map { it.json }
    }

    // curl 'localhost:8080/component' -X PUT -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"componentId":2,"name":"aa","type":1,"description":"bb","cost":10,"image":null,"count":10}'
    @PutMapping(WHICH)
    fun update(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return if (when (which) {
            COMPONENT -> componentRepo.update(json.component)
            CLIENT -> clientRepo.update(json.client)
//            EMPLOYEE_INFO -> employeeInfoRepo.update(json.employeeInfo)
//            MANAGER, DELIVERY_WORKER, ADMINISTRATOR -> false
//            ORDER -> orderRepo.update(json.order)
            else -> false
        }) responseOk else responseBadRequest
    }

    // curl 'localhost:8080/component?id=2' -X DELETE -H 'Auth-credentials: admin:admin'
    @DeleteMapping(WHICH)
    fun delete(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return if (when (which) {
            COMPONENT -> componentRepo.delete(id)
            CLIENT -> clientRepo.delete(id)
//            EMPLOYEE_INFO -> clientRepo.delete(id)
//            MANAGER, DELIVERY_WORKER, ADMINISTRATOR -> employeesRepo.delete(id, which)
//            ORDER -> orderRepo.delete(id)
            else -> false
        }) responseOk else responseBadRequest
    }
}
