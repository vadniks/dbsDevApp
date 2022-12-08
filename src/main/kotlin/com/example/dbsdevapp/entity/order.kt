package com.example.dbsdevapp.entity

import org.springframework.jdbc.core.RowMapper

data class Order(
    val orderId: Int?,
    val clientId: Int,
    val managerId: Int?,
    val deliveryWorkerId: Int?,
    val cost: Int,
    val count: Int,
    val created: Int,
    val completed: Int?
) : IEntity { override val json get() = JsonImpl().apply {
    put(ORDER_ID, orderId)
    put(CLIENT_ID, clientId)
    put(MANAGER_ID, managerId)
    put(DELIVERY_WORKER_ID, deliveryWorkerId)
    put(COST, cost)
    put(COUNT, count)
    put(CREATED, created)
    put(COMPLETED, completed)
} }

const val ORDER_ID = "orderId"
const val CLIENT_ID = "clientId"
const val MANAGER_ID = "managerId"
const val DELIVERY_WORKER_ID = "deliveryWorkerId"
const val CREATED = "creationDatetime"
const val COMPLETED = "completionDatetime"
const val ORDERS = "orders"

val orderMapper = RowMapper<Order> { resultSet, _ -> Order(
    resultSet.getNullableInt(ORDER_ID),
    resultSet.getNullableInt(CLIENT_ID)!!,
    resultSet.getNullableInt(MANAGER_ID),
    resultSet.getNullableInt(DELIVERY_WORKER_ID),
    resultSet.getNullableInt(COST)!!,
    resultSet.getNullableInt(COUNT)!!,
    resultSet.getNullableInt(CREATED)!!,
    resultSet.getNullableInt(COMPLETED)
) }
