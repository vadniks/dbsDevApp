package com.example.dbsdevapp.entity

data class BoughtComponent(
    val componentId: Int,
    val orderId: Int,
    val clientId: Int
)

const val COMPONENT_ID = "componentId"
