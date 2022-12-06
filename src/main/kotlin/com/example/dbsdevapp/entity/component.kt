package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import com.example.dbsdevapp.getTyped
import org.springframework.jdbc.core.RowMapper

data class Component(
    val id: Int?,
    val name: String,
    val type: ComponentType,
    val description: String,
    val cost: Int,
    val image: String?,
    val count: Int
) : IEntity { override val json get() = HashMap<String, Any?>().apply {
    put(COMPONENT_ID, id)
    put(NAME, name)
    put(TYPE, type.type)
    put(DESCRIPTION, description)
    put(COST, cost)
    put(IMAGE, image)
    put(COUNT, count)
} }

val Json.component get() = Component(
    getTyped(COMPONENT_ID),
    getTyped(NAME)!!,
    getTyped<Int?>(TYPE)?.toComponentType()!!,
    getTyped(DESCRIPTION)!!,
    getTyped(COST)!!,
    getTyped(IMAGE),
    getTyped(COUNT)!!
)

const val COMPONENT_ID = "componentId"
const val TYPE = "type"
const val DESCRIPTION = "description"
const val COST = "cost"
const val IMAGE = "image"
const val COUNT = "count"
const val COMPONENTS = "components"
const val COMPONENT = "component"

enum class ComponentType(val type: Int, val title: String) {
    CPU (0, "Processor"),
    MB  (1, "Motherboard"),
    GPU (2, "Graphics adapter"),
    RAM (3, "Operating memory"),
    HDD (4, "Hard drive"),
    SSD (5, "Solid state drive"),
    PSU (6, "Power supply unit"),
    FAN (7, "Cooler"),
    CASE(8, "Case")
}

fun Int.toComponentType() = ComponentType.values().find { it.type == this }

val componentMapper = RowMapper<Component> { resultSet, _ -> Component(
    resultSet.getNullableInt(COMPONENT_ID),
    resultSet.getNullableString(NAME)!!,
    resultSet.getNullableInt(TYPE)?.toComponentType()!!,
    resultSet.getNullableString(DESCRIPTION)!!,
    resultSet.getNullableInt(COST)!!,
    resultSet.getNullableString(IMAGE),
    resultSet.getNullableInt(COUNT)!!
) }
