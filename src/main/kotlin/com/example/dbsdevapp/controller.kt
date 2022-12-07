package com.example.dbsdevapp

import com.example.dbsdevapp.entity.*
import com.example.dbsdevapp.repo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import kotlin.math.absoluteValue
import kotlin.random.Random

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
    private val orderRepo: OrderRepo,
    private val boughtComponentRepo: BoughtComponentRepo
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

    // curl 'localhost:8080/newComponent' -X POST -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"componentId":null,"name":"aa","type":1,"description":"bb","cost":10,"image":null,"count":1}'
    @PostMapping("/newComponent")
    fun newComponent(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ) = responseForbidden.authenticated(MANAGER, credentials)
    { if (componentRepo.insert(json.component)) responseOk else responseBadRequest }

    // curl 'localhost:8080/newClient' -X POST -H 'Content-Type: application/json' -d '{"clientId":null,"name":"client1","surname":"_","phone":1000000000,"address":"_","email":"client1@email.com","password":"pass"}'
    @PostMapping("/newClient")
    fun newClient(@RequestBody json: Json)
    = if (clientRepo.insert(json.client)) responseOk else responseBadRequest

    // curl 'localhost:8080/newEmployee' -X POST -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"employeeId":null,"name":"manager","surname":"_","phone":1000000001,"email":"manager@email.com","password":"pass","salary":100,"jobType":0}'
    // curl 'localhost:8080/newEmployee' -X POST -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"employeeId":null,"name":"delivery","surname":"_","phone":1000000002,"email":"delivery@email.com","password":"pass","salary":100,"jobType":1}'
    @Transactional
    @PostMapping("/newEmployee")
    fun newEmployee(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ) = responseForbidden.authenticated(MANAGER, credentials) {
        val employeeInfo = json.employeeInfo

        if (!employeeInfoRepo.insert(employeeInfo))
            return@authenticated responseBadRequest

        val employeeId = employeeInfoRepo.get(employeeInfo.email)
            ?: return@authenticated responseBadRequest

        if (when (employeeInfo.jobType) {
            JobType.MANAGER -> employeesRepo.insert(Manager(employeeId), MANAGERS)
            JobType.DELIVERY_WORKER -> employeesRepo.insert(DeliveryWorker(employeeId), DELIVERY_WORKERS)
        }) responseOk else responseBadRequest
    }

    // curl 'localhost:8080/newOrder?clientId=1&componentIds=3,6' -X POST -H 'Auth-credentials: client1:pass'
    @Suppress("NAME_SHADOWING")
    @Transactional
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
            val component = componentRepo.get(i) ?: return responseBadRequest
            components.add(component)
            cost += component.cost
            count++
        }

        val created = System.currentTimeMillis().toInt().absoluteValue

        if (!orderRepo.insert(Order(
            null, client.id!!,
            null, null,
            cost, count, created, null
        ))) return responseBadRequest

        val orderId = orderRepo.get1(clientId, created)?.orderId ?: return responseBadRequest

        for (i in componentIds)
            if (!boughtComponentRepo.insert(BoughtComponent(i, orderId, clientId)))
                return responseBadRequest

        return responseOk
    }

    // curl 'localhost:8080/assignEmployeesToOrder?orderId=4&clientId=1&managerId=1&deliveryWorkerId=7' -X POST -H 'Auth-credentials: manager:pass'
    @Transactional
    @PostMapping("/assignEmployeesToOrder")
    fun assignEmployeesToOrder(
        @RequestParam orderId: Int,
        @RequestParam clientId: Int,
        @RequestParam managerId: Int,
        @RequestParam deliveryWorkerId: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ): VoidResponse = responseForbidden.authenticated(MANAGER, credentials) {
        if (orderRepo.assignEmployeesToOrder(orderId, clientId, managerId, deliveryWorkerId)) responseOk
        else responseBadRequest
    }

    // curl 'localhost:8080/completeOrder?orderId=4&clientId=1' -X POST -H 'Auth-credentials: manager:pass'
    @Transactional
    @PostMapping("/completeOrder")
    fun completeOrder(
        @RequestParam orderId: Int,
        @RequestParam clientId: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = responseForbidden.authenticated(MANAGER, credentials) {
        orderRepo.get(orderId, clientId) ?: return@authenticated responseBadRequest
        if (orderRepo.completeOrder(orderId, clientId, System.currentTimeMillis().toInt().absoluteValue))
            responseOk
        else
            responseBadRequest
    }

    // curl 'localhost:8080/getComponent?id=3'
    @ResponseBody
    @GetMapping("/getComponent")
    fun getComponent(@RequestParam id: Int) = componentRepo.get(id)?.json

    // curl 'localhost:8080/getClient?id=1' -H 'Auth-credentials: manager:pass'
    @ResponseBody
    @GetMapping("/getClient")
    fun getClient(
        @RequestParam id: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = { clientRepo.get(id)?.json }.run {
        null.authenticated(DELIVERY_WORKER, credentials, this)
            ?: null.authenticated(MANAGER, credentials, this)
    }

    // curl 'localhost:8080/getEmployee?id=1' -H 'Auth-credentials: manager:pass'
    @ResponseBody
    @GetMapping("/getEmployee")
    fun getEmployee(
        @RequestParam id: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = null.authenticated(MANAGER, credentials) { employeeInfoRepo.get(id) }

    // curl 'localhost:8080/getAllComponents'
    @ResponseBody
    @GetMapping("/getAllComponents")
    fun getAllComponents() = componentRepo.get().map { it.json }

    // curl 'localhost:8080/getAllClients' -H 'Auth-credentials: manager:pass'
    @ResponseBody
    @GetMapping("/getAllClients")
    fun getAllClients(@RequestHeader(AUTH_CREDENTIALS) credentials: String)
    = emptyList<Json>().authenticated(MANAGER, credentials) { clientRepo.get().map { it.json } }

    // curl 'localhost:8080/getUserOrders?clientId=1' -H 'Auth-credentials: client1:pass'
    @ResponseBody
    @GetMapping("/getUserOrders")
    fun getUserOrders(
        @RequestParam clientId: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = { orderRepo.get(clientId).map { it.json } }.run {
        null.authenticated(CLIENT, credentials, this)
            ?: null.authenticated(MANAGER, credentials, this)
            ?: emptyList<Json>()
    }
        //emptyList<Json>().authenticated(CLIENT, credentials) { orderRepo.get(clientId).map { it.json } }

    /*
    { orderRepo.get(clientId).map { it.json } }.apply {
        null.authenticated(CLIENT, credentials, this)
            ?: null.authenticated(MANAGER, credentials, this)
            ?: emptyList<Json>()
    }
    */

    //
    @ResponseBody
    @GetMapping("/getOrderedComponents")
    fun getOrderedComponents(
        @RequestParam orderId: Int,
        @RequestParam clientId: Int,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ) = { boughtComponentRepo.get(orderId, clientId) }.run {
        null.authenticated(MANAGER, credentials, this)
            ?: null.authenticated(DELIVERY_WORKER, credentials, this)
            ?: null.authenticated(CLIENT, credentials, this)
            ?: emptyList<Json>()
    }

    // curl 'localhost:8080/component' -X PUT -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"componentId":2,"name":"aa","type":1,"description":"bb","cost":10,"image":null,"count":10}'
    @PutMapping("/updateComponent")
    fun updateComponent(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ) = responseForbidden.authenticated(MANAGER, credentials)
    { if (componentRepo.update(json.component)) responseOk else responseBadRequest }

    @PutMapping("/updateClient")
    fun updateClient(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ) = responseForbidden.authenticated(CLIENT, credentials)
    { if (clientRepo.update(json.client)) responseOk else responseBadRequest }

    @PutMapping("/updateEmployee")
    fun updateEmployee(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ) = responseForbidden.authenticated(MANAGER, credentials)
    { if (employeeInfoRepo.update(json.employeeInfo)) responseOk else responseBadRequest }

    // curl 'localhost:8080/component?id=2' -X DELETE -H 'Auth-credentials: admin:admin'
    @Transactional
    @DeleteMapping("/deleteComponent")
    fun deleteComponent(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ) = responseForbidden.authenticated(MANAGER, credentials) {
        for (boughtComponent in boughtComponentRepo.get(id))
            boughtComponentRepo.delete(boughtComponent)
        componentRepo.delete(id)
    }

    @Transactional
    @DeleteMapping("/deleteClient")
    fun deleteClient(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ) = responseForbidden.authenticated(MANAGER, credentials) {
        var result = false
        for (order in orderRepo.get(id)) {
            for (boughtComponent in boughtComponentRepo.get(order.orderId!!, order.clientId))
                boughtComponentRepo.delete(boughtComponent)
            result = orderRepo.delete(order.orderId, order.clientId)
        }
        if (result) responseOk else responseBadRequest
    }

    @Transactional
    @DeleteMapping("/deleteEmployee")
    fun deleteEmployee(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ) = responseForbidden.authenticated(MANAGER, credentials) {
        val employeeInfo = employeeInfoRepo.get(id) ?: return@authenticated responseBadRequest

        for (order in orderRepo.get(id, employeeInfo.jobType.table))
            orderRepo.setEmployeeId(order.orderId!!, order.clientId, null, employeeInfo.jobType.job)

        employeesRepo.delete(id, employeeInfo.jobType.table)
        employeeInfoRepo.delete(id)
    }

    @Transactional
    @DeleteMapping("/deleteOrder")
    fun deleteOrder(
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam orderId: Int,
        @RequestParam clientId: Int
    ) = responseForbidden.authenticated(MANAGER, credentials) {
        for (i in boughtComponentRepo.get(orderId, clientId))
            boughtComponentRepo.delete(i)
        orderRepo.delete(orderId, clientId)
    }
}
