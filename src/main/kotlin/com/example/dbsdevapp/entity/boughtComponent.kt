package com.example.dbsdevapp.entity

import org.springframework.jdbc.core.RowMapper

data class BoughtComponent(
    val componentId: Int,
    val orderId: Int
) : IEntity { override val json get() = throw UnsupportedOperationException() }

const val BOUGHT_COMPONENTS = "boughtComponents"

val boughtComponentMapper = RowMapper<BoughtComponent> { resultSet, _ -> BoughtComponent(
    resultSet.getNullableInt(COMPONENT_ID)!!,
    resultSet.getNullableInt(ORDER_ID)!!
) }
