package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import com.example.dbsdevapp.tryCatch
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class EmployeesRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(employee: IEmployee, table: String)
    = template.update("insert into $table($EMPLOYEE_ID) values(?)", employee.id) == 1

    fun get(id: Int, which: String) = null.tryCatch { template.queryForObject(
        "select * from $which where $EMPLOYEE_ID = ?",
        employeeMappers.employee(which), id
    ) }

    fun get(which: String): List<IEmployee> = template.query(
        "select * from ${which + 's'}",
        employeeMappers.employee(which)
    )

    fun delete(id: Int, which: String) = template.update("delete from $which where $EMPLOYEE_ID = ?", id) == 1
}
