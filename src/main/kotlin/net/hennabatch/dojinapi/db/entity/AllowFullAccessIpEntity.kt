package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.AllowFullAccessIp
import net.hennabatch.dojinapi.db.table.AllowFullAccessIpTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AllowFullAccessIpEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<AllowFullAccessIpEntity>(AllowFullAccessIpTable)

    val ip by AllowFullAccessIpTable.ip
    val createdAt by AllowFullAccessIpTable.createdAt
    val updatedAt by AllowFullAccessIpTable.updatedAt

    fun toModel(): AllowFullAccessIp = AllowFullAccessIp(
        id = id.value,
        ip = ip,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}