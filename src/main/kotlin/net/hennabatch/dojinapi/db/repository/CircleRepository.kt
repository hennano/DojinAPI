package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.entity.CircleEntity
import net.hennabatch.dojinapi.db.model.Circle
import org.jetbrains.exposed.dao.with

object CircleRepository {

    fun select(id: Int, resoleDepth: Int = 1): Circle {
        return CircleEntity[id].toModel(resoleDepth)
    }

    fun selectAll(resoleDepth: Int = 1):List<Circle>{
        return CircleEntity.all().with(CircleEntity::members).map { it.toModel(resoleDepth) }
    }
}