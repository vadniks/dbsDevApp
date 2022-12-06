package com.example.dbsdevapp.entity

data class EmployeeInfo(
    val id: Int?,
    val name: String,
    val surname: String,
    val phone: Int,
    val email: String,
    val password: String,
    val salary: Int,
    val jobType: JobType
)

const val SALARY = "salary"
const val JOB_TYPE = "jobType"

enum class JobType(val type: Int) {
    MANAGER(0),
    DELIVERY_WORKER(1),
    ADMINISTRATOR(2)
}
