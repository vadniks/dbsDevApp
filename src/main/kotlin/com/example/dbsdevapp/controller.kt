package com.example.dbsdevapp

import com.example.dbsdevapp.entity.*
import com.example.dbsdevapp.repo.ClientRepo
import com.example.dbsdevapp.repo.ComponentRepo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private typealias VoidResponse = ResponseEntity<Void>
private val responseOk = VoidResponse(HttpStatus.OK)
private val responseBadRequest = VoidResponse(HttpStatus.BAD_REQUEST)
private const val AUTH_CREDENTIALS = "Auth-credentials"
typealias Json = Map<String, Any?>
private const val WHICH = "/{which}"
inline fun <reified T : Any?> Json.getTyped(key: String) = get(key) as T

@RestController
class Controller(
    private val componentRepo: ComponentRepo,
    private val clientRepo: ClientRepo
) {

    // curl 'localhost:8080/insert/component' -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"componentId":null,"name":"aa","type":1,"description":"bb","cost":10,"image":null,"count":1}'
    @PostMapping("/insert$WHICH")
    fun insert(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return if (when (which) {
            COMPONENT -> componentRepo.insert(json.component)
            CLIENT -> clientRepo.insert(json.client)
            else -> false
        }) responseOk else responseBadRequest
    }

    // curl 'localhost:8080/get/component?id=1' -H 'Auth-credentials: admin:admin'
    @ResponseBody
    @GetMapping("/get$WHICH")
    fun get(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ): Json? {
        if (credentials != "admin:admin") return null
        return when (which) {
            COMPONENT -> componentRepo.get(id)
            CLIENT -> clientRepo.get(id)
            else -> null
        }?.json
    }

    // curl 'localhost:8080/getAll/component' -H 'Auth-credentials: admin:admin'
    @ResponseBody
    @GetMapping("/getAll$WHICH")
    fun getAll(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ): List<Json> {
        if (credentials != "admin:admin") return emptyList()
        return when (which) {
            COMPONENT -> componentRepo.get()
            CLIENT -> clientRepo.get()
            else -> emptyList()
        }.map { it.json }
    }

    // curl 'localhost:8080/update/component' -X PUT -H 'Auth-credentials: admin:admin' -H 'Content-Type: application/json' -d '{"componentId":2,"name":"aa","type":1,"description":"bb","cost":10,"image":null,"count":10}'
    @PutMapping("/update$WHICH")
    fun update(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return if (when (which) {
            COMPONENT -> componentRepo.update(json.component)
            CLIENT -> clientRepo.update(json.client)
            else -> false
        }) responseOk else responseBadRequest
    }

    // curl 'localhost:8080/delete/component?id=2' -X DELETE -H 'Auth-credentials: admin:admin'
    @DeleteMapping("/delete$WHICH")
    fun delete(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return if (when (which) {
            COMPONENT -> componentRepo.delete(id)
            CLIENT -> clientRepo.delete(id)
            else -> false
        }) responseOk else responseBadRequest
    }
}
