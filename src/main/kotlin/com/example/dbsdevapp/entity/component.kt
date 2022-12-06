package com.example.dbsdevapp.entity

data class Component(
    val id: Int?,
    val name: String,
    val type: ComponentType,
    val description: String,
    val cost: Int,
    val image: String?,
    val count: Int
)

const val ID = "id"
const val TYPE = "type"
const val DESCRIPTION = "description"
const val COST = "cost"
const val IMAGE = "image"
const val COUNT = "count"

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

fun Int.toType() = ComponentType.values().find { it.type == this }

const val LENGTH_SHORT = 64
const val LENGTH_MIDDLE = 128
const val LENGTH_LONG = 512
