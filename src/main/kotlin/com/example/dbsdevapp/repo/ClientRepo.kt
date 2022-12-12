package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import com.example.dbsdevapp.tryCatch
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ClientRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(client: Client) = template.update(
        """insert into $CLIENTS($NAME, $SURNAME, $PHONE, $ADDRESS, $EMAIL, $PASSWORD)
           values(?, ?, ?, ?, ?, ?)""".trimMargin(),
        client.name, client.surname, client.phone,
        client.address, client.email, client.password
    ) == 1

    fun get(id: Int) = null.tryCatch { template.queryForObject(
        "select * from $CLIENTS where $CLIENT_ID = ?", clientMapper, id) }

    fun get(): List<Client> = template.query("select * from $CLIENTS", clientMapper)

    fun get(email: String, password: String) = null.tryCatch { template.queryForObject(
        "select * from $CLIENTS where $EMAIL = ? and $PASSWORD = ?",
        clientMapper,
        email, password
    ) }

    fun update(client: Client) = template.update(
        """update $CLIENTS set $NAME = ?, $SURNAME = ?, $PHONE = ?, $ADDRESS = ?, $EMAIL = ?, $PASSWORD = ?
           where $CLIENT_ID = ?""".trimMargin(),
        client.name, client.surname, client.phone,
        client.address, client.email, client.password,
        client.id
    ) == 1

    fun delete(id: Int) = template.update("delete from $CLIENTS where $CLIENT_ID = ?", id) == 1
}
