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

    fun get(id: Int) = template.queryForObject(
        "select * from $BOUGHT_COMPONENTS where $CLIENT_ID = ?", boughtComponentMapper, id)

    fun get(): List<BoughtComponent> = template.query("select * from $BOUGHT_COMPONENTS", boughtComponentMapper)

    fun update(boughtComponent: BoughtComponent) = template.update(
        """update $BOUGHT_COMPONENTS set $NAME = ?, $SURNAME = ?, $PHONE = ?, $ADDRESS = ?, $EMAIL = ?, $PASSWORD = ?
           where $CLIENT_ID = ?""".trimMargin(),
        boughtComponent.name, boughtComponent.surname, boughtComponent.phone,
        boughtComponent.address, boughtComponent.email, boughtComponent.password,
        boughtComponent.id
    ) == 1

    fun delete(id: Int) = template.update("delete from $BOUGHT_COMPONENTS where $CLIENT_ID = ?", id) == 1
}
