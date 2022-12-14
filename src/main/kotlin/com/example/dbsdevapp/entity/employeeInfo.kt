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
) : IEntity { override val json get() = JsonImpl().apply {
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
    getTyped(NAME)!!,
    getTyped(SURNAME)!!,
    getTyped(PHONE)!!,
    getTyped(EMAIL)!!,
    getTyped(PASSWORD)!!,
    getTyped(SALARY)!!,
    getTyped<Int?>(JOB_TYPE)?.jobType!!
)

const val EMPLOYEE_ID = "employeeId"
const val SALARY = "salary"
const val JOB_TYPE = "jobType"
const val EMPLOYEE_INFO = "employeeInfo"

enum class JobType(val type: Int) {
    MANAGER(0),
    DELIVERY_WORKER(1)
}

val Int.jobType get() = JobType.values().find { it.type == this }

val String.jobType get() = when (this) {
    MANAGER -> JobType.MANAGER
    DELIVERY_WORKER -> JobType.DELIVERY_WORKER
    else -> null
}

val JobType.table get() = when (this) {
    JobType.MANAGER -> MANAGERS
    JobType.DELIVERY_WORKER -> DELIVERY_WORKERS
}

val JobType.job get() = when (this) {
    JobType.MANAGER -> MANAGER
    JobType.DELIVERY_WORKER -> DELIVERY_WORKER
}

val employeeInfoMapper = RowMapper<EmployeeInfo> { resultSet, _ -> EmployeeInfo(
    resultSet.getNullableInt(EMPLOYEE_ID),
    resultSet.getNullableString(NAME)!!,
    resultSet.getNullableString(SURNAME)!!,
    resultSet.getNullableInt(PHONE)!!,
    resultSet.getNullableString(EMAIL)!!,
    resultSet.getNullableString(PASSWORD)!!,
    resultSet.getNullableInt(SALARY)!!,
    resultSet.getNullableInt(JOB_TYPE)?.jobType!!
) }
