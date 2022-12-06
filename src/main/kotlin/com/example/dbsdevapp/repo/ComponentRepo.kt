package com.example.dbsdevapp.repo

import com.example.dbsdevapp.entity.Component
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ComponentRepo(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val template: JdbcTemplate
) {

    fun insert(component: Component) = template.update(
        """insert into components(componentId, name, type, description, cost, image, count)
           values(?, ?, ?, ?, ?, ?, ?)""",
        component.id,
        component.name,
        component.type.type,
        component.description,
        component.cost,
        component.image,
        component.count
    ) == 1
}
