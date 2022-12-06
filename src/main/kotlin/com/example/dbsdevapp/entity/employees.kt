package com.example.dbsdevapp.entity

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

interface IEmployee : IEntity {
    override val json get() = throw UnsupportedOperationException()
    val id: Int
    val name get() = this::class.simpleName!!
}

data class Manager(override val id: Int) : IEmployee
data class DeliveryWorker(override val id: Int) : IEmployee

const val MANAGER = "manager"
const val DELIVERY_WORKER = "deliveryWorker"

private fun <T : IEmployee> employeeMapper(clazz: KClass<T>, resultSet: ResultSet) =
    clazz.primaryConstructor?.call(resultSet.getNullableInt(EMPLOYEE_ID)!!)!!

val employeeMappers = arrayOf(
    MANAGER to RowMapper { rs, _ -> employeeMapper(Manager::class, rs) },
    DELIVERY_WORKER to RowMapper { rs, _ -> employeeMapper(DeliveryWorker::class, rs) }
)

fun Array<Pair<String, RowMapper<out IEmployee>>>.employee(which: String)
= find { it.first == which }!!.second
