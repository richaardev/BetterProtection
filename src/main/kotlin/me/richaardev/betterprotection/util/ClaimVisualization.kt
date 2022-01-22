package me.richaardev.betterprotection.util

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.dao.Claim
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.VisualizationManager
import me.richaardev.helper.utils.scheduler.CoroutineTask
import me.richaardev.helper.utils.scheduler.schedule
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

enum class VisualizationType {
    CLAIM,
    CLAIMING,

    OVERRIDING
}

class ClaimVisualization(
    val player: Player, val claim: Claim, val type: VisualizationType = VisualizationType.CLAIM
    ) {
    val elements = mutableMapOf<Location, Material>()

    var task: CoroutineTask?
    init {
        createVisualization()

        task = BetterProtection.INSTANCE.schedule {
            while (true) {
                if (!claim.contains(player.location)) {
                    if (claim.corners.all {
                            Location(player.world, it.x.toDouble(), player.location.y, it.z.toDouble()).distance(player.location) >= 15
                        }
                    ) return@schedule this@ClaimVisualization.destroy()
                }
                updateElements()
                waitFor(20 * 1)
            }
        }

    }


    fun createVisualization() {
        claim.corners.forEach {
            val world = claim.world
            val y = world.getHighestBlockAt(it.x, it.z).location.y + 1

            val location = Location(
                world, it.x.toDouble(), y, it.z.toDouble()
            )


            // TODO: Fazer com que o verifique se o player está vendo o bloco para caso ele estiver em caverna ou etc...

            player.sendBlockChange(location, Material.GLOWSTONE.createBlockData())
            elements[location] = Material.GLOWSTONE

            val toCheck = listOf(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)
            val block = world.getBlockAt(it.x, y.toInt(), it.z)
            for (face in toCheck) {
                val loc = block.getRelative(face).location
                if (claim.contains(block.getRelative(face).location)) {
                    elements[loc] = Material.GOLD_BLOCK
                    player.sendBlockChange(block.getRelative(face).location, Material.GOLD_BLOCK.createBlockData())
                }
            }
        }
    }

    fun updateElements() {
        elements.forEach {
            player.sendBlockChange(it.key, it.value.createBlockData())
        }
    }

    fun destroy() {
        elements.forEach {
            player.sendBlockChange(it.key, it.key.world!!.getBlockAt(it.key).type.createBlockData())
        }
        elements.clear()
        task?.cancel()

        VisualizationManager.removeVisualization(this)
    }

}