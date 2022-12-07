package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import com.example.dbsdevapp.getTyped
import com.example.dbsdevapp.log
import com.example.dbsdevapp.tryCatch
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.stereotype.Repository
import org.springframework.util.LinkedCaseInsensitiveMap
import kotlin.math.absoluteValue

@Repository
class OrderRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(order: Order) = template.update(
        """insert into $ORDERS($CLIENT_ID, $MANAGER_ID, $DELIVERY_WORKER_ID, $COST, $COUNT, $CREATED, $COMPLETED)
           values(?, ?, ?, ?, ?, ?, ?)""".trimMargin().apply { log.info(order.toString()) },
        order.clientId, order.managerId, order.deliveryWorkerId,
        order.cost, order.count, order.created,
        order.completed
    ) == 1

    fun get(orderId: Int, clientId: Int) = null.tryCatch { template.queryForObject(
        "select * from $ORDERS where $ORDER_ID = ? and $CLIENT_ID = ?",
        orderMapper, orderId, clientId
    ) }

    fun get(clientId: Int): List<Order> = template.query(
        "select * from $ORDERS where $CLIENT_ID = ?",
        orderMapper, clientId
    )

    fun get1(clientId: Int, created: Int) = null.tryCatch { template.queryForObject(
        "select * from $ORDERS where $CLIENT_ID = ? and $CREATED = ?", orderMapper, clientId, created) }

    fun get(employeeId: Int, which: String): List<Order> = template.query(
        "select * from $ORDERS where ${
                when (which) {
                    MANAGER -> MANAGER_ID
                    DELIVERY_WORKER -> DELIVERY_WORKER_ID
                    else -> throw IllegalArgumentException()
                }
            } = ?",
        orderMapper, employeeId
    )

    fun countAll() = (((SimpleJdbcCall(template)
        .withProcedureName("countOrders")
        .execute()
        .values
        .iterator()
        .next() as List<*>)[0]!! as Map<*, *>)
        .values
        .iterator()
        .next() as Long)
        .toInt()
        .absoluteValue

    fun assignEmployeesToOrder(
        orderId: Int,
        clientId: Int,
        managerId: Int,
        deliveryWorkerId: Int
    ) = template.update(
        "update $ORDERS set $MANAGER_ID = ?, $DELIVERY_WORKER_ID = ? where $ORDER_ID = ? and $CLIENT_ID = ?",
        managerId, deliveryWorkerId, orderId, clientId
    ) == 1

    fun completeOrder(orderId: Int, clientId: Int, completed: Int) = template.update(
        "update $ORDERS set $COMPLETED = ? where $ORDER_ID = ? and $CLIENT_ID = ?",
        completed, orderId, clientId
    ) == 1

    fun setEmployeeId(orderId: Int, clientId: Int, employeeId: Int?, which: String) = template.update(
        "update $ORDERS set ${
            when (which) {
                MANAGER -> MANAGER_ID
                DELIVERY_WORKER -> DELIVERY_WORKER_ID
                else -> throw IllegalArgumentException()
            }
        } = ? where $ORDER_ID = ? and $CLIENT_ID = ?",
        employeeId, orderId, clientId
    ) == 1

    fun delete(orderId: Int, clientId: Int) = template.update(
        "delete from $ORDERS where $ORDER_ID = ? and $CLIENT_ID = ?",
        orderId, clientId
    ) == 1
}
