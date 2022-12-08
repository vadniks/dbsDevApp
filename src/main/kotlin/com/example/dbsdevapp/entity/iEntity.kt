package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import java.sql.ResultSet

interface IEntity {
    val json: Json
}

typealias JsonImpl = HashMap<String, Any?>

@Suppress("USELESS_CAST")
fun ResultSet.getNullableInt(columnLabel: String) = getObject(columnLabel, Int::class.java) as Int?
@Suppress("USELESS_CAST")
fun ResultSet.getNullableString(columnLabel: String) = getString(columnLabel) as String?

val List<out IEntity>.json get() = map { it.json }
