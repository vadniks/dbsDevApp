package com.example.dbsdevapp.entity

data class Client(
    val id: Int?,
    val name: String,
    val surname: String,
    val phone: Int,
    val address: String,
    val email: String,
    val password: String
)

const val NAME = "name"
const val SURNAME = "surname"
const val PHONE = "phone"
const val ADDRESS = "address"
const val EMAIL = "email"
const val PASSWORD = "password"
