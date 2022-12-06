package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class OrderRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(order: Order) = template.update(
        """insert into $ORDERS($CLIENT_ID, $MANAGER_ID, $DELIVERY_WORKER_ID, $COST, $COUNT, $CREATED, $COMPLETED)
           values(?, ?, ?, ?, ?, ?, ?)""".trimMargin(),
        order.clientId, order.managerId, order.deliveryWorkerId,
        order.cost, order.count, order.created,
        order.completed
    ) == 1

    fun get(id: Int) = template.queryForObject(
        "select * from $ORDERS where $ORDER_ID = ?", orderMapper, id)

    fun get(): List<Order> = template.query("select * from $ORDERS", orderMapper)

    fun update(order: Order) = template.update(
        """update $ORDERS set $CLIENT_ID = ?, $MANAGER_ID = ?, $DELIVERY_WORKER_ID = ?, $COST = ?, $COUNT = ?, $CREATED = ?, $COMPLETED = ?
           where $ORDER_ID = ?""".trimMargin(),
        order.clientId, order.managerId, order.deliveryWorkerId,
        order.cost, order.count, order.created,
        order.completed
    ) == 1

    fun delete(id: Int) = template.update("delete from $ORDERS where $ORDER_ID = ?", id) == 1
}
