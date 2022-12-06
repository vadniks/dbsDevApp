package com.example.dbsdevapp.entity

import com.example.dbsdevapp.Json
import com.example.dbsdevapp.getTyped
import org.springframework.jdbc.core.RowMapper

data class EmployeeInfo(
    val id: Int?,
    val name: String,
    val surname: String,
    val phone: Int,
    val email: String,
    val password: String,
    val salary: Int,
    val jobType: JobType
) : IEntity { override val json get() = HashMap<String, Any?>().apply {
    put(EMPLOYEE_ID, id)
    put(NAME, name)
    put(SURNAME, surname)
    put(PHONE, phone)
    put(EMAIL, email)
    put(PASSWORD, password)
    put(SALARY, salary)
    put(JOB_TYPE, jobType)
} }

val Json.employeeInfo get() = EmployeeInfo(
    getTyped(EMPLOYEE_ID),
    getTyped(NAME),
    getTyped(SURNAME),
    getTyped(PHONE),
    getTyped(EMAIL),
    getTyped(PASSWORD),
    getTyped(SALARY),
    getTyped(JOB_TYPE)
)

const val EMPLOYEE_ID = "employeeId"
const val SALARY = "salary"
const val JOB_TYPE = "jobType"
const val EMPLOYEE_INFO = "employeeInfo"

enum class JobType(val type: Int) {
    MANAGER(0),
    DELIVERY_WORKER(1),
    ADMINISTRATOR(2)
}

val Int.jobType get() = JobType.values().find { it.type == this }

val employeeInfoMapper = RowMapper<EmployeeInfo> { resultSet, _ -> EmployeeInfo(
    resultSet.getInt(EMPLOYEE_ID),
    resultSet.getString(NAME),
    resultSet.getString(SURNAME),
    resultSet.getInt(PHONE),
    resultSet.getString(EMAIL),
    resultSet.getString(PASSWORD),
    resultSet.getInt(SALARY),
    resultSet.getInt(JOB_TYPE).jobType!!
) }
