package com.example.dbsdevapp

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.*
import java.util.logging.Logger

@SpringBootApplication
class DbsDevAppApplication

@Suppress("unused")
val Any?.unit get() = Unit

fun <T> T?.tryCatch(throwable: () -> T) = try { throwable() } catch (_: Exception) { this }

@Suppress("unused")
val log = Logger.getLogger("a")!!

fun main(vararg args: String) = SpringApplication(DbsDevAppApplication::class.java).apply {
    setDefaultProperties(Properties().apply {
        setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/db")
        setProperty("spring.datasource.username", "server")
        setProperty("spring.datasource.password", "server")
    })
}.run(*args).unit
