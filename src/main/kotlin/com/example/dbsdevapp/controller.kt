package com.example.dbsdevapp

import com.example.dbsdevapp.entity.*
import com.example.dbsdevapp.repo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import kotlin.random.Random
import kotlin.reflect.KClass

private typealias VoidResponse = ResponseEntity<Void>
private val responseOk = VoidResponse(HttpStatus.OK)
private val responseBadRequest = VoidResponse(HttpStatus.BAD_REQUEST)
private val responseForbidden = VoidResponse(HttpStatus.FORBIDDEN)
private val responseServerError = VoidResponse(HttpStatus.INTERNAL_SERVER_ERROR)
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

    @Suppress("NAME_SHADOWING")
    private fun checkRoleCredentials(role: String, credentials: String): Boolean {
        val credentials = credentials.split(':')
        if (credentials.size != 2) return false

        return (when (role) {
            CLIENT -> clientRepo.get(credentials[0], credentials[1])
            MANAGER, DELIVERY_WORKER -> employeeInfoRepo.get(
                credentials[0], credentials[1],
                role.jobType ?: return false
            )
            else -> null
        }) != null
    }

    private inline fun <T> T.authenticated(role: String, credentials: String, crossinline action: () -> T)
    = if (checkRoleCredentials(role, credentials)) action() else this

    // curl 'localhost:8080/component' -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"componentId":null,"name":"aa","type":1,"description":"bb","cost":10,"image":null,"count":1}'
    @PostMapping("/newComponent")
    fun newComponent(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ) = responseForbidden.authenticated(MANAGER, credentials)
    { if (componentRepo.insert(json.component)) responseOk else responseBadRequest }

    @PostMapping("/newClient")
    fun newClient(@RequestBody json: Json)
    = if (componentRepo.insert(json.component)) responseOk else responseBadRequest

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

        if (employeeInfo.jobType != which.jobType)
            return responseBadRequest

        if (!employeeInfoRepo.insert(employeeInfo))
            return responseBadRequest

        val employeeId = employeeInfoRepo.get(employeeInfo.email)
            ?: return responseBadRequest

        return if (employeesRepo.insert(when (which) {
            MANAGER -> Manager(employeeId)
            DELIVERY_WORKER -> DeliveryWorker(employeeId)
            else -> return responseBadRequest
        })) responseOk else responseBadRequest
    }

    @Suppress("NAME_SHADOWING")
    @PostMapping("/newOrder")
    fun newOrder(
        @RequestParam clientId: Int,
        @RequestParam componentIds: IntArray,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ): VoidResponse {
        val client = clientRepo.get(clientId) ?: return responseBadRequest

        val credentials = credentials.split(':')
        if (client.name != credentials[0] || client.password != credentials[1])
            return responseForbidden

        val components = ArrayList<Component>()
        var cost = 0
        var count = 0

        for (i in componentIds) {
            val component = componentRepo.get(i)
            components.add(component ?: return responseBadRequest)
            cost += component.cost
            count++
        }

        val created = System.currentTimeMillis().toUInt().toInt()
        val random = Random(created)

        return if (orderRepo.insert(Order(
            null, client.id!!,
            random.nextInt(employeesRepo.get(MANAGER).size),
            random.nextInt(employeesRepo.get(DELIVERY_WORKER).size),
            cost, count, created, null
        ))) responseOk else responseBadRequest
    }

    @PostMapping("/completeOrder")
    fun completeOrder(
        @RequestHeader orderId: Int,
        @RequestParam clientId: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = responseForbidden.authenticated(MANAGER, credentials) {
        orderRepo.get(orderId, clientId) ?: return@authenticated responseBadRequest
        if (orderRepo.completeOrder(orderId, clientId, System.currentTimeMillis().toUInt().toInt()))
            responseOk
        else
            responseBadRequest
    }

    // curl 'localhost:8080/component?id=1' -H 'Auth-credentials: admin:admin'
    @ResponseBody
    @GetMapping("/getComponent")
    fun getComponent(@RequestParam id: Int) = componentRepo.get(id)?.json

    @ResponseBody
    @GetMapping("/getClient")
    fun getClient(
        @RequestParam id: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = { clientRepo.get(id)?.json }.run {
        null.authenticated(DELIVERY_WORKER, credentials, this)
            ?: null.authenticated(MANAGER, credentials, this)
    }

    @ResponseBody
    @GetMapping("/getEmployee")
    fun getEmployee(
        @RequestParam id: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = null.authenticated(MANAGER, credentials) { employeeInfoRepo.get(id) }

    // curl 'localhost:8080/all/component' -H 'Auth-credentials: admin:admin'
    @ResponseBody
    @GetMapping("/getAllComponents")
    fun getAllComponents() = componentRepo.get().map { it.json }

    @ResponseBody
    @GetMapping("/getAllComponents")
    fun getAllClients(@RequestHeader(AUTH_CREDENTIALS) credentials: String)
    = emptyList<Json>().authenticated(MANAGER, credentials) { clientRepo.get().map { it.json } }

    fun getAllOrders() {

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
            EMPLOYEE_INFO -> employeeInfoRepo.update(json.employeeInfo)
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
