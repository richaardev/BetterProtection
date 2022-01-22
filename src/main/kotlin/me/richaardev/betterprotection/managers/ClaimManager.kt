package me.richaardev.betterprotection.managers

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.dao.Claim
import me.richaardev.betterprotection.dao.ClaimFlag
import me.richaardev.betterprotection.dao.User
import me.richaardev.betterprotection.tables.claim.Claims
import me.richaardev.betterprotection.util.Border
import me.richaardev.helper.extensions.asOffline
import me.richaardev.helper.extensions.playSound
import org.bukkit.*
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.max
import kotlin.math.min

object ClaimManager {
    val claims = mutableListOf<Claim>()

    fun createClaim(player: Player, corner1: Border, corner2: Border) {
        transaction(BetterProtection.db) {
            val user = User.findById(player.uniqueId) ?: User.new(player.uniqueId) {}
            val playerRemainingBlocks = user.remainingBlocks

            if (!isValidTerrain(corner1, corner2)) {
                player.playSound(Sound.ENTITY_VILLAGER_NO)
                return@transaction player.sendMessage("§cVocê não pode criar um terreno menor que §a5x5")
            }

            if (playerRemainingBlocks <= getClaimSize(corner1, corner2)) {
                player.playSound(Sound.ENTITY_VILLAGER_NO)
                player.sendMessage("§cVocê não tem blocos suficientes para criar este terreno")
                player.sendMessage("§c  Blocos sobrando: §b${playerRemainingBlocks}")
                player.sendMessage("§c  Tamanho do terreno: §b${getClaimSize(corner1, corner2)}")
                return@transaction
            }

            if (true) {
                val minX = min(corner1.x, corner2.x)
                val minZ = min(corner1.z, corner2.z)
                val maxX = max(corner1.x, corner2.x)
                val maxZ = max(corner1.z, corner2.z)

                for (x in minX..maxX) {
                    for (z in minZ..maxZ) {
                        val overridingClaim = claims.firstOrNull { it.contains(Location(player.world, x.toDouble(), 60.0, z.toDouble())) }
                        if (overridingClaim != null) {
                            player.playSound(Sound.ENTITY_VILLAGER_NO)
                            player.sendMessage("§cA área que você está tentando criar está sobreposta ao terreno de §b${overridingClaim.owner.name}§c!")
                            return@transaction
                        }
                    }
                }
            }


            val claim = Claim.new {
                this.owner = player.asOffline()
                this.world = player.world
                this.corner1 = serializeCorner(corner1)
                this.corner2 = serializeCorner(corner2)
                this.originalFlags = ClaimFlag.new {}
            }

            VisualizationManager.createVisualization(player, claim)
            claims.add(claim)
            player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
            player.sendMessage("§aSeu terreno foi criado e protegido com sucesso!")
        }
    }


    fun removeClaim(claim: Claim) {
        transaction(BetterProtection.db) {
            claims.remove(claim)
            VisualizationManager.removeVisualization(claim)
            claim.delete()
        }
    }
    fun getClaims(player: OfflinePlayer): List<Claim> {
        return transaction(BetterProtection.db) {
            return@transaction Claim.find { Claims.owner eq player.uniqueId }.toMutableList()
        }
    }
    fun getClaimsFrom(player: OfflinePlayer): List<Claim> {
        return transaction(BetterProtection.db) {
            return@transaction claims.filter { it.owner.uniqueId == player.uniqueId }
        }
    }


    fun getClaimAt(location: Location): Claim? {
        return claims.firstOrNull {
                it.world.name == location.world?.name &&
//            (it.corners.any { Location(location.world, it.x.toDouble(), location.y, it.z.toDouble()).distance(location) < 200 }) &&
                it.contains(location)
            }

    }
    fun hasClaimAt(location: Location): Boolean {
        return getClaimAt(location) != null
    }

    fun isValidTerrain(corner1: Border, corner2: Border): Boolean {
        val sizeX = Math.abs(corner1.x - corner2.x)
        val sizeZ = Math.abs(corner1.z - corner2.z)

        val minSize = 4
        return sizeX >= minSize && sizeZ >= minSize
    }

    fun getClaimSize(c1: Border, c2: Border): Int {
        val minX = min(c1.x, c2.x)
        val minZ = min(c1.z, c2.z)

        val maxX = max(c1.x, c2.x) + 1
        val maxZ = max(c1.z, c2.z) + 1

        return (maxX - minX) * (maxZ - minZ)
    }








    fun serializeCorner(corner1: Border): String {
        return "${corner1.x}|${corner1.z}"
    }
    fun deserializeCorner(corner: String): Border {
        val split = corner.split("|")
        return Border(split[0].toInt(), split[1].toInt())
    }
}