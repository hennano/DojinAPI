package net.hennabatch.dojinapi.test

import net.hennabatch.dojinapi.db.table.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.xml.validation.Schema

class InitDB {

    companion object {
        fun createAllTable(){
            transaction {
                SchemaUtils.create(AuthorAliasTable)
                SchemaUtils.create(AuthorTable)
                SchemaUtils.create(BookSeriesTable)
                SchemaUtils.create(BookTable)
                SchemaUtils.create(CharacterTable)
                SchemaUtils.create(CircleAliasTable)
                SchemaUtils.create(CircleTable)
                SchemaUtils.create(EventSeriesTable)
                SchemaUtils.create(EventTable)
                SchemaUtils.create(MAuthorCircleTable)
                SchemaUtils.create(MBookCharacters)
                SchemaUtils.create(MBookOriginalTable)
                SchemaUtils.create(OriginalTable)
            }
        }

        fun dropAllTable(){
            transaction {
                SchemaUtils.drop(MAuthorCircleTable)
                SchemaUtils.drop(MBookCharacters)
                SchemaUtils.drop(MBookOriginalTable)
                SchemaUtils.drop(BookTable)
                SchemaUtils.drop(AuthorAliasTable)
                SchemaUtils.drop(BookSeriesTable)
                SchemaUtils.drop(EventTable)
                SchemaUtils.drop(EventSeriesTable)
                SchemaUtils.drop(CircleAliasTable)
                SchemaUtils.drop(CharacterTable)
                SchemaUtils.drop(CircleTable)
                SchemaUtils.drop(OriginalTable)
                SchemaUtils.drop(AuthorTable)
            }
        }
    }
}