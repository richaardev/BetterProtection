package me.richaardev.betterprotection.tables

import me.richaardev.betterprotection.BetterProtection
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import java.util.*

object Users : IdTable<UUID>() {
    override val id: Column<EntityID<UUID>>
        get() = uniqueId

    val uniqueId = uuid("id").entityId()
    val blocks = integer("blocks").default(BetterProtection.conf.initialBlocks)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(uniqueId)
}