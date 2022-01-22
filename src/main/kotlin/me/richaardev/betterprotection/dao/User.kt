package me.richaardev.betterprotection.dao

import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.tables.Users
import org.bukkit.Bukkit
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    val blocks by Users.blocks

    val remainingBlocks: Int
        get() {
            val user = Bukkit.getOfflinePlayer(id.value)
            val claims = ClaimManager.getClaimsFrom(user).sumOf { it.area }
            return blocks - claims
        }
}