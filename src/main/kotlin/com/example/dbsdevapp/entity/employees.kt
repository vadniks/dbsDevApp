package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import com.example.dbsdevapp.getTyped
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

private fun Json.employeeId() = getTyped<Int>(EMPLOYEE_ID)
private fun map(id: Int) = HashMap<String, Any?>().apply { put(EMPLOYEE_ID, id) }

interface IEmployee : IEntity {
    override val json get() = map(id)
    val id: Int
    val name get() = this::class.simpleName!!
}

data class Manager(override val id: Int) : IEmployee
data class DeliveryWorker(override val id: Int) : IEmployee
data class Administrator(override val id: Int) : IEmployee

val MANAGER = Manager::class.simpleName!!
val DELIVERY_WORKER = DeliveryWorker::class.simpleName!!
val ADMINISTRATOR = Administrator::class.simpleName!!

val Json.manager get() = Manager(employeeId()!!)
val Json.deliveryWorker get() = DeliveryWorker(employeeId()!!)
val Json.administrator get() = Administrator(employeeId()!!)

private fun <T : IEmployee> employeeMapper(clazz: KClass<T>, resultSet: ResultSet) =
    clazz.primaryConstructor?.call(resultSet.getNullableInt(EMPLOYEE_ID)!!)!!

val employeeMappers = arrayOf(
    MANAGER to RowMapper { rs, _ -> employeeMapper(Manager::class, rs) },
    DELIVERY_WORKER to RowMapper { rs, _ -> employeeMapper(DeliveryWorker::class, rs) },
    ADMINISTRATOR to RowMapper { rs, _ -> employeeMapper(Administrator::class, rs) }
)

fun Array<Pair<String, RowMapper<out IEmployee>>>.employee(which: String)
= find { it.first == which }!!.second
