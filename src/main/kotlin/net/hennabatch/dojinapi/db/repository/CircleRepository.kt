package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.entity.CircleEntity
import net.hennabatch.dojinapi.db.model.Circle
import org.jetbrains.exposed.dao.with

object CircleRepository {

    fun selectAll(resoleDepth: Int = 1):List<Circle>{
        return CircleEntity.all().with(CircleEntity::members).map { it.toModel(resoleDepth) }
    }
}