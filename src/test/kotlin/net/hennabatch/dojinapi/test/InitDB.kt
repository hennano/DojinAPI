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
    }
}