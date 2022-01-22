package me.richaardev.betterprotection.tables.claim

import org.jetbrains.exposed.dao.id.IntIdTable

object Claims : IntIdTable() {
    val owner = uuid("owner").index()

    val world = text("world_name")
    val corner1 = text("corner1")
    val corner2 = text("corner2")

    val flags = reference("flags", ClaimsFlags)


    // TODO: Blocked players and whitelisted players

}