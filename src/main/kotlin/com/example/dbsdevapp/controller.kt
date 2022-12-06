package com.example.dbsdevapp

import com.example.dbsdevapp.entity.*
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
class Controller(private val componentRepo: ComponentRepo) {

    @PostMapping("/insert$WHICH")
    fun insert(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return when (which) {
            COMPONENT -> if (componentRepo.insert(json.component)) responseOk else responseBadRequest
            else -> responseBadRequest
        }
    }

    @ResponseBody
    @GetMapping("/get$WHICH")
    fun get(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ): Json? {
        if (credentials != "admin:admin") return null
        return when (which) {
            COMPONENT -> componentRepo.get(id)?.json
            else -> null
        }
    }

    @ResponseBody
    @GetMapping("/getAll$WHICH")
    fun getAll(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String
    ): List<Json> {
        if (credentials != "admin:admin") return emptyList()
        return when (which) {
            COMPONENT -> componentRepo.get()
            else -> null
        }
    }

    @PutMapping("/update$WHICH")
    fun update(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestBody json: Json
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return when (which) {
            COMPONENT -> if (componentRepo.update(json.component)) responseOk else responseBadRequest
            else -> responseBadRequest
        }
    }

    @DeleteMapping("/delete$WHICH")
    fun delete(
        @PathVariable which: String,
        @RequestHeader(AUTH_CREDENTIALS) credentials: String,
        @RequestParam id: Int
    ): VoidResponse {
        if (credentials != "admin:admin") return responseBadRequest
        return when (which) {
            COMPONENT -> if (componentRepo.delete(id)) responseOk else responseBadRequest
            else -> responseBadRequest
        }
    }
}
