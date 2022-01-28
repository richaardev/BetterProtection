package me.richaardev.betterprotection.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.util.Border
import me.richaardev.betterprotection.util.ClaimSelection
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.helper.expansions.scheduler
import me.richaardev.helper.scheduler.schedule
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.concurrent.TimeUnit

class SelectionListener : Listener {
    val terrainSelections = Caffeine.newBuilder()
        .expireAfterWrite(2, TimeUnit.MINUTES)
        .build<Player, ClaimSelection>()
        .asMap()

    @EventHandler
    fun interactEvent(e: PlayerInteractEvent) {
        val item = e.player.itemInHand

        if (item != null && item.type != Material.AIR && e.clickedBlock != null && e.hand == EquipmentSlot.HAND) {
            if (item.type == BetterProtection.conf.protectionTool) {
                e.isCancelled = true

                // Eu estou usando o scheduler para não ter erro do usuário clicar em um bloco e não ficar o "diamante"
                scheduler().schedule(BetterProtection.INSTANCE) {
                    waitFor(2)

                    var selection = terrainSelections[e.player]
                    if (selection != null) {
                        selection.pos2 = e.clickedBlock!!
                        val pos1 = selection.pos1!!.location
                        val pos2 = selection.pos2!!.location
                        val corner1 = Border(pos1.x.toInt(), pos1.z.toInt())
                        val corner2 = Border(pos2.x.toInt(), pos2.z.toInt())


                        terrainSelections.remove(e.player)

                        ClaimManager.createClaim(e.player, corner1, corner2)

                        e.player.sendBlockChange(pos1, pos1.world!!.getBlockAt(pos1).blockData)
                    } else {
                        selection = ClaimSelection(e.player)
                        selection.pos1 = e.clickedBlock!!
                        e.player.sendBlockChange(e.clickedBlock!!.location, Material.DIAMOND_BLOCK.createBlockData())
                        terrainSelections[e.player] = selection
                    }
                }
            }
        }
    }
}