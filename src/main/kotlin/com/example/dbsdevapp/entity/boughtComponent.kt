package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import com.example.dbsdevapp.getTyped
import org.springframework.jdbc.core.RowMapper

data class BoughtComponent(
    val componentId: Int,
    val orderId: Int,
    val clientId: Int
) : IEntity { override val json get() = HashMap<String, Any?>().apply {
    put(COMPONENT_ID, componentId)
    put(ORDER_ID, orderId)
    put(CLIENT_ID, clientId)
} }

val Json.boughtComponent get() = BoughtComponent(
    getTyped(COMPONENT_ID)!!,
    getTyped(ORDER_ID)!!,
    getTyped(CLIENT_ID)!!
)

const val BOUGHT_COMPONENT = "boughtComponent"
const val BOUGHT_COMPONENTS = "boughtComponents"

val boughtComponentMapper = RowMapper<BoughtComponent> { resultSet, _ -> BoughtComponent(
    resultSet.getNullableInt(COMPONENT_ID)!!,
    resultSet.getNullableInt(ORDER_ID)!!,
    resultSet.getNullableInt(CLIENT_ID)!!
) }
