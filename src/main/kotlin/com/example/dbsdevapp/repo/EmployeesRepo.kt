package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class EmployeesRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(employee: IEmployee)
    = template.update("insert into ${employee.name}($EMPLOYEE_ID) values(?)", employee.id) == 1

    fun get(id: Int, which: String) = template.queryForObject(
        "select * from $which where $EMPLOYEE_ID = ?",
        employeeMappers.employee(which), id
    )

    fun get(which: String): List<IEmployee> = template.query(
        "select * from $EMPLOYEE_INFO",
        employeeMappers.employee(which)
    )

    fun delete(id: Int, which: String) = template.update("delete from $which where $EMPLOYEE_ID = ?", id) == 1
}
