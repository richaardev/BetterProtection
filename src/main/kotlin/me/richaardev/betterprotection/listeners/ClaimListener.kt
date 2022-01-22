package me.richaardev.betterprotection.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import me.richaardev.betterprotection.managers.ClaimManager
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Door
import org.bukkit.block.data.type.TrapDoor
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.time.Duration

class ClaimListener : Listener {
    val alertedPlayers = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(5))
        .build<Player, Long>()
        .asMap()
    fun sendErrorMessage(player: Player, message: String) {
        if (alertedPlayers.containsKey(player)) return
        player.sendMessage(message)
        alertedPlayers[player] = System.currentTimeMillis()
    }

    @EventHandler
    fun playerInteractEvent(e: PlayerInteractEvent) {
        val action = e.action
        if (action == Action.LEFT_CLICK_AIR) return;

        val clickedBlock = e.clickedBlock
        val clickedBlockType = clickedBlock?.type ?: Material.AIR
        val hand = e.hand

        val player = e.player as Player
        val claim = clickedBlock?.location?.let { ClaimManager.getClaimAt(it) } ?: return

        val error = "§cVocê não tem a permissão de §b${claim.owner.name} §cpara interagir nessa claim"

        if (action == Action.PHYSICAL) {
            if (clickedBlockType != Material.TURTLE_EGG) return
            if (!claim.canBuild(player)) {
                sendErrorMessage(player, error)
                e.isCancelled = true
                return
            }
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (clickedBlockType.isInteractable) {
                if (!claim.canUse(player) && !(claim.getFlags().openDoors && isTrapdoorOrDoor(clickedBlock))) {
                    sendErrorMessage(player, error)
                    e.isCancelled = true
                    return
                }
            }
        }
    }
    @EventHandler
    fun blockBreakEvent(e: BlockBreakEvent) {
        val player = e.player
        val block = e.block

        val claim = ClaimManager.getClaimAt(block.location)

        val error = "§cVocê não tem a permissão de §b${claim?.owner?.name} §cpara destruir nessa claim"
        if (claim != null && !claim.canBuild(player)) {
            sendErrorMessage(player, error)
            e.isCancelled = true
            return
        }
    }
    @EventHandler
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        val player = e.player
        val block = e.block

        val claim = ClaimManager.getClaimAt(block.location)

        val error = "§cVocê não tem a permissão de §b${claim?.owner?.name} §cpara construir nessa claim"
        if (claim != null && !claim.canBuild(player)) {
            sendErrorMessage(player, error)
            e.isCancelled = true
            return
        }
    }

    @EventHandler
    fun entityDamageEvent(e: EntityDamageByEntityEvent) {
        val entity = e.entity
        val player = e.damager as? Player ?: return

        val claim = ClaimManager.getClaimAt(entity.location)
        if (claim != null && !claim.canUse(player) && entity !is Monster && entity !is Player) {
            sendErrorMessage(player, "§cVocê não tem a permissão de §b${claim.owner.name} §cpara dar dano nessa claim")
            e.isCancelled = true
            return
        }
    }

    @EventHandler
    fun blockFromToEvent(e: BlockFromToEvent) {
        val claim = ClaimManager.getClaimAt(e.toBlock.location)
        if (claim != null && !claim.contains(e.block.location)) e.isCancelled = true
    }


    fun isTrapdoorOrDoor(block: Block): Boolean {
        return (block.blockData is TrapDoor || block.blockData is Door)
    }
}