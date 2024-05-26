package net.hennabatch.dojinapi.logic

import net.hennabatch.dojinapi.db.DatabaseSingleton.dbQuery
import net.hennabatch.dojinapi.db.model.Circle
import net.hennabatch.dojinapi.db.repository.CircleRepository

class CircleControllerLogic {
    suspend fun fetchCircles(): List<Circle> {
        return dbQuery{
            CircleRepository.selectAll(0)
        }

    }
}