package net.hennabatch.dojinapi.logic

import net.hennabatch.dojinapi.db.model.Circle
import net.hennabatch.dojinapi.db.repository.CircleRepository

class CircleControllerLogic {
    fun fetchCircles(): List<Circle> {
        return CircleRepository.selectAll(0)
    }
}