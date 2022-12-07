package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import java.sql.ResultSet

interface IEntity {
    val json: Json
}

fun ResultSet.getNullableInt(columnLabel: String) = getObject(columnLabel, Int::class.java) as Int?
fun ResultSet.getNullableString(columnLabel: String) = getString(columnLabel) as String?
