package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ComponentRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(component: Component) = template.update(
        """insert into $COMPONENTS($COMPONENT_ID, $NAME, $TYPE, $DESCRIPTION, $COST, $IMAGE, $COUNT)
           values(?, ?, ?, ?, ?, ?, ?)""".trimMargin(),
        component.id, component.name, component.type.type,
        component.description, component.cost, component.image,
        component.count
    ) == 1

    fun get(id: Int) = template.queryForObject(
        "select * from $COMPONENTS where $COMPONENT_ID = ?", componentMapper, id)

    fun get() = template.queryForObject("select * from $COMPONENTS", componentMapper)

    fun update(component: Component) = template.update(
        """update $COMPONENTS set $NAME = ?, $TYPE = ?, $DESCRIPTION = ?, $COST = ?, $IMAGE = ?, $COUNT = ?
           where $COMPONENT_ID = ?""".trimMargin(),
        component.name, component.type.type, component.description,
        component.cost, component.image, component.count,
        component.id
    ) == 1

    fun delete(id: Int) = template.update("delete from $COMPONENTS where $COMPONENT_ID = ?", id) == 1
}
