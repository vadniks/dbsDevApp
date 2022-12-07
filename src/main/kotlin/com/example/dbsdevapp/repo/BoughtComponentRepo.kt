package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class BoughtComponentRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(boughtComponent: BoughtComponent) = template.update(
        """insert into $BOUGHT_COMPONENTS($COMPONENT_ID, $ORDER_ID, $CLIENT_ID)
           values(?, ?, ?)""".trimMargin(),
        boughtComponent.componentId, boughtComponent.orderId, boughtComponent.clientId
    ) == 1

    fun get(orderId: Int, clientId: Int): List<BoughtComponent> = template.query(
        "select * from $BOUGHT_COMPONENTS where $ORDER_ID = ? and $CLIENT_ID = ?",
        boughtComponentMapper,
        orderId, clientId
    )

    fun get(componentId: Int): List<BoughtComponent> = template.query(
        "select * from $BOUGHT_COMPONENTS where $COMPONENT_ID = ?",
        boughtComponentMapper, componentId
    )

    fun delete(boughtComponent: BoughtComponent) = template.update(
        "delete from $BOUGHT_COMPONENTS where $COMPONENT_ID = ? and $ORDER_ID = ? and $CLIENT_ID = ?",
        boughtComponent.componentId, boughtComponent.orderId, boughtComponent.clientId
    ) == 1
}
