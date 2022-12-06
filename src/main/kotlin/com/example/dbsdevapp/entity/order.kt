package com.example.dbsdevapp.entity

data class Order(
    val orderId: Int,
    val clientId: Int,
    val managerId: Int,
    val deliveryWorkerId: Int,
    val cost: Int,
    val count: Int,
    val created: Int,
    val completed: Int?
)

const val ORDER_ID = "orderId"
const val CLIENT_ID = "clientId"
const val MANAGER_ID = "managerId"
const val DELIVERY_WORKER_ID = "deliveryWorkerId"
const val CREATED = "created"
const val COMPLETED = "completed"
