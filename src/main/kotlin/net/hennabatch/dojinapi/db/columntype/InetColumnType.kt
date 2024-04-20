package net.hennabatch.dojinapi.db.columntype

import org.jetbrains.exposed.sql.ColumnType
import java.net.InetAddress

class InetColumnType: ColumnType() {
    override fun sqlType():String = "inet"

    override fun valueFromDB(value: Any): InetAddress = when(value){
        is String -> InetAddress.getByName(value)
        else -> error("Unexpected value of type InetAddress: $value of${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Any): Any = when(value){
        is InetAddress -> value.toString()
        else -> error("Unexpected value of type InetAddress: $value of${value::class.qualifiedName}")
    }
}
