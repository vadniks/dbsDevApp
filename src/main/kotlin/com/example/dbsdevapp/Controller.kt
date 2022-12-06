package com.example.dbsdevapp

import com.example.dbsdevapp.entity.Component
import com.example.dbsdevapp.entity.ComponentType
import com.example.dbsdevapp.repo.ComponentRepo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

typealias VoidResponse = ResponseEntity<Void>
private val responseOk = VoidResponse(HttpStatus.OK)
private val responseBadRequest = VoidResponse(HttpStatus.BAD_REQUEST)

@RestController
class Controller(private val componentRepo: ComponentRepo) {

    @PostMapping("/a")
    fun insert(): VoidResponse {
        return if (componentRepo.insert(Component(null, "a", ComponentType.CPU, "b", 1, null, 1)))
            responseOk else responseBadRequest
    }
}
