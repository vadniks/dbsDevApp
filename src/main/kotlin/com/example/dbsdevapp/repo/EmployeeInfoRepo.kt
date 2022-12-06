package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class EmployeeInfoRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(employeeInfo: EmployeeInfo) = template.update(
        """insert into $EMPLOYEE_INFO($NAME, $SURNAME, $PHONE, $EMAIL, $PASSWORD, $SALARY, $JOB_TYPE)
           values(?, ?, ?, ?, ?, ?, ?)""".trimMargin(),
        employeeInfo.name, employeeInfo.surname, employeeInfo.phone,
        employeeInfo.email, employeeInfo.password, employeeInfo.salary,
        employeeInfo.jobType
    ) == 1

    fun get(id: Int) = template.queryForObject(
        "select * from $EMPLOYEE_INFO where $EMPLOYEE_ID = ?", employeeInfoMapper, id)

    fun get(email: String) = template.queryForObject(
        "select $EMPLOYEE_ID from $EMPLOYEE_INFO where $EMPLOYEE_ID = ?", Integer::class.java) as Int

    fun get(): List<EmployeeInfo> = template.query("select * from $EMPLOYEE_INFO", employeeInfoMapper)

    fun update(employeeInfo: EmployeeInfo) = template.update(
        """update $EMPLOYEE_INFO set $NAME = ?, $SURNAME = ?, $PHONE = ?, $EMAIL = ?, $PASSWORD = ?, $SALARY = ?, $JOB_TYPE = ?
           where $EMPLOYEE_ID = ?""".trimMargin(),
        employeeInfo.name, employeeInfo.surname, employeeInfo.phone, employeeInfo.email,
        employeeInfo.password, employeeInfo.salary, employeeInfo.jobType, employeeInfo.id
    ) == 1

    fun delete(id: Int) = template.update("delete from $EMPLOYEE_INFO where $EMPLOYEE_ID = ?", id) == 1
}
