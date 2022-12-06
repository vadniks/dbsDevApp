package com.example.dbsdevapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.*

@SpringBootApplication
class DbsDevAppApplication

val Any?.unit get() = Unit

fun main(vararg args: String) = SpringApplication(DbsDevAppApplication::class.java).apply {
    setDefaultProperties(Properties().apply {
        setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/db")
        setProperty("spring.datasource.username", "root")
        setProperty("spring.datasource.password", "root")
    })
}.run(*args).unit
