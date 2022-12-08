package com.example.dbsdevapp.repo

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.stereotype.Repository

@Repository
class OtherRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun databaseInformation() = SimpleJdbcCall(template)
        .withProcedureName("databaseInformation")
        .execute()
        .run { "${this["_version"]} ${this["currentDate"]} ${this["currentTime"]} ${this["currentUser"]}" }
}
