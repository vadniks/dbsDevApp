package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import com.example.dbsdevapp.getTyped
import org.springframework.jdbc.core.RowMapper

data class Client(
    val id: Int?,
    val name: String,
    val surname: String,
    val phone: Int,
    val address: String,
    val email: String,
    val password: String
) : IEntity { override val json get() = HashMap<String, Any?>().apply {
    put(CLIENT_ID, id)
    put(NAME, name)
    put(SURNAME, surname)
    put(PHONE, phone)
    put(ADDRESS, address)
    put(EMAIL, email)
    put(PASSWORD, password)
} }

val Json.client get() = Client(
    getTyped(CLIENT_ID),
    getTyped(NAME),
    getTyped(SURNAME),
    getTyped(PHONE),
    getTyped(ADDRESS),
    getTyped(EMAIL),
    getTyped(PASSWORD)
)

const val NAME = "name"
const val SURNAME = "surname"
const val PHONE = "phone"
const val ADDRESS = "address"
const val EMAIL = "email"
const val PASSWORD = "password"
const val CLIENTS = "clients"
const val CLIENT = "client"

val clientMapper = RowMapper<Client> { resultSet, _ -> Client(
    resultSet.getInt(CLIENT_ID),
    resultSet.getString(NAME),
    resultSet.getString(SURNAME),
    resultSet.getInt(PHONE),
    resultSet.getString(ADDRESS),
    resultSet.getString(EMAIL),
    resultSet.getString(PASSWORD)
) }
