package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.table.MAuthorCircleTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert

object MAuthorCircleRepository {
    fun insert(authorId: Int, circleId: Int){
        MAuthorCircleTable.insert {
            it[this.authorId] = authorId
            it[this.circleId] = circleId
        }
    }

    fun delete(authorId: Int, circleId: Int):Boolean {
        return MAuthorCircleTable.deleteWhere {
            (MAuthorCircleTable.authorId eq authorId) and
                    (MAuthorCircleTable.circleId eq circleId)
        } > 0
    }
}