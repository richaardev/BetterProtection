package me.richaardev.betterprotection.dao

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.tables.claim.Claims
import me.richaardev.betterprotection.util.Border
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.tables.ClaimsTrusts
import me.richaardev.helper.database.delegate.offlinePlayer
import me.richaardev.helper.database.delegate.world
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.max
import kotlin.math.min

class Claim(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Claim>(Claims)

    var owner by offlinePlayer(Claims.owner)
    var world by world(Claims.world)
    var corner1 by Claims.corner1
    var corner2 by Claims.corner2
    var originalFlags by ClaimFlag referencedOn Claims.flags

    fun getFlags(): ClaimFlag {
        return transaction(BetterProtection.db) {
            return@transaction originalFlags
        }
    }
    val corners: List<Border>
        get() {
            val cn1 = ClaimManager.deserializeCorner(corner1)
            val cn2 = ClaimManager.deserializeCorner(corner2)

            val minX = min(cn1.x, cn2.x)
            val maxX = max(cn1.x, cn2.x)

            val minZ = min(cn1.z, cn2.z)
            val maxZ = max(cn1.z, cn2.z)

            val c1 = Border(minX, minZ)
            val c2 = Border(maxX, maxZ)
            val c3 = Border(minX, maxZ)
            val c4 = Border(maxX, minZ)

            return listOf(c1, c2, c3, c4)
        }
    val center: Border
        get() {
            val corner1 = ClaimManager.deserializeCorner(corner1)
            val corner2 = ClaimManager.deserializeCorner(corner2)

            val x = (corner1.x + corner2.x) / 2
            val z = (corner1.z + corner2.z) / 2
            return Border(x, z)
        }
    val area: Int
        get () {
            val cn1 = ClaimManager.deserializeCorner(corner1)
            val cn2 = ClaimManager.deserializeCorner(corner2)

            val minX = min(cn1.x, cn2.x)
            val maxX = max(cn1.x, cn2.x)

            val minZ = min(cn1.z, cn2.z)
            val maxZ = max(cn1.z, cn2.z)

            return ((maxX) - minX) * (maxZ - minZ)
        }

    fun canManage(player: Player): Boolean {
        return player.uniqueId == owner.uniqueId || player.hasPermission("betterprotection.admin")
    }
    fun canBuild(player: Player): Boolean {
        return player.uniqueId == owner.uniqueId || getTrusts().any { it._id == player.uniqueId && it.build } || BetterProtection.ignoringClaims.contains(player.uniqueId)
    }
    fun canUse(player: Player): Boolean {
        return player.uniqueId == owner.uniqueId || getTrusts().any { it._id == player.uniqueId && it.build } || BetterProtection.ignoringClaims.contains(player.uniqueId)
    }

    fun contains(location: Location): Boolean {
        val c1 = ClaimManager.deserializeCorner(corner1)
        val c2 = ClaimManager.deserializeCorner(corner2)

        val minX = min(c1.x, c2.x)
        val minZ = min(c1.z, c2.z)

        val maxX = max(c1.x, c2.x) + 1
        val maxZ = max(c1.z, c2.z) + 1

        val inClaim = location.x >= minX && location.x < maxX &&
                location.z >= minZ && location.z < maxZ

        return inClaim
    }

    fun getTrusts(): List<ClaimTrust> {
        return transaction(BetterProtection.db) {
            return@transaction ClaimTrust.find { ClaimsTrusts.claim eq this@Claim.id }.toList()
        }
    }
}