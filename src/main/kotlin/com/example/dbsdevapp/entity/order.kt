package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import com.example.dbsdevapp.getTyped
import org.springframework.jdbc.core.RowMapper

data class Order(
    val orderId: Int,
    val clientId: Int,
    val managerId: Int,
    val deliveryWorkerId: Int,
    val cost: Int,
    val count: Int,
    val created: Int,
    val completed: Int?
) : IEntity { override val json = HashMap<String, Any?>().apply {
    put(ORDER_ID, orderId)
    put(CLIENT_ID, clientId)
    put(MANAGER_ID, managerId)
    put(DELIVERY_WORKER_ID, deliveryWorkerId)
    put(COST, cost)
    put(COUNT, count)
    put(CREATED, created)
    put(COMPLETED, completed)
} }

val Json.order get() = Order(
    getTyped(ORDER_ID)!!,
    getTyped(CLIENT_ID)!!,
    getTyped(MANAGER_ID)!!,
    getTyped(DELIVERY_WORKER_ID)!!,
    getTyped(COST)!!,
    getTyped(COUNT)!!,
    getTyped(CREATED)!!,
    getTyped(COMPLETED)
)

const val ORDER_ID = "orderId"
const val CLIENT_ID = "clientId"
const val MANAGER_ID = "managerId"
const val DELIVERY_WORKER_ID = "deliveryWorkerId"
const val CREATED = "created"
const val COMPLETED = "completed"
const val ORDERS = "orders"
const val ORDER = "order"

val orderMapper = RowMapper<Order> { resultSet, _ -> Order(
    resultSet.getNullableInt(ORDER_ID)!!,
    resultSet.getNullableInt(CLIENT_ID)!!,
    resultSet.getNullableInt(MANAGER_ID)!!,
    resultSet.getNullableInt(DELIVERY_WORKER_ID)!!,
    resultSet.getNullableInt(COST)!!,
    resultSet.getNullableInt(COUNT)!!,
    resultSet.getNullableInt(CREATED)!!,
    resultSet.getNullableInt(COMPLETED)
) }
