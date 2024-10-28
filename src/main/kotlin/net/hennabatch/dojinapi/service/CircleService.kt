package net.hennabatch.dojinapi.service

import kotlinx.serialization.json.JsonObject
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.logic.CircleServiceLogic
import net.hennabatch.dojinapi.views.CircleResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CircleService: KoinComponent {
    val db by inject<CommonDb>()

    val circleServiceLogic by inject<CircleServiceLogic>()
    val circleResponse by inject<CircleResponse>()

    suspend fun getCircles(): JsonObject{
        return db.dbQuery {
            val circles = circleServiceLogic.fetchCircles()
            return@dbQuery circleResponse.makeCircleListFetched(circles)
        }
    }
}