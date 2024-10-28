package net.hennabatch.dojinapi.init

import net.hennabatch.dojinapi.db.table.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class InitDB {

    companion object {
        fun createAllTable(){
            transaction {
                SchemaUtils.create(AuthorAliasTable)
                SchemaUtils.create(AuthorTable)
                SchemaUtils.create(BookSeriesTable)
                SchemaUtils.create(RatingTable)
                SchemaUtils.create(BookTable)
                SchemaUtils.create(BookMarkTable)
                SchemaUtils.create(CharacterTable)
                SchemaUtils.create(CircleAliasTable)
                SchemaUtils.create(CircleTable)
                SchemaUtils.create(EventSeriesTable)
                SchemaUtils.create(EventTable)
                SchemaUtils.create(MAuthorCircleTable)
                SchemaUtils.create(MBookCharacterTable)
                SchemaUtils.create(MBookOriginalTable)
                SchemaUtils.create(MBookGuestAuthorTable)
                SchemaUtils.create(OriginalClosureTable)
                SchemaUtils.create(OriginalTable)
                SchemaUtils.create(AllowFullAccessIpTable)
            }
        }

        fun dropAllTable(){
            transaction {
                SchemaUtils.drop(MAuthorCircleTable)
                SchemaUtils.drop(MBookCharacterTable)
                SchemaUtils.drop(MBookOriginalTable)
                SchemaUtils.drop(MBookGuestAuthorTable)
                SchemaUtils.drop(BookMarkTable)
                SchemaUtils.drop(BookTable)
                SchemaUtils.drop(RatingTable)
                SchemaUtils.drop(AuthorAliasTable)
                SchemaUtils.drop(BookSeriesTable)
                SchemaUtils.drop(EventTable)
                SchemaUtils.drop(EventSeriesTable)
                SchemaUtils.drop(CircleAliasTable)
                SchemaUtils.drop(CharacterTable)
                SchemaUtils.drop(CircleTable)
                SchemaUtils.drop(OriginalClosureTable)
                SchemaUtils.drop(OriginalTable)
                SchemaUtils.drop(AuthorTable)
                SchemaUtils.drop(AllowFullAccessIpTable)
            }
        }
    }
}