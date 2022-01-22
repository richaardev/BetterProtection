package me.richaardev.betterprotection.listeners

import me.richaardev.betterprotection.managers.ClaimManager
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.*

class ClaimFlagsListener : Listener {
    @EventHandler
    fun onEntityExplode(e: EntityExplodeEvent) {
        e.blockList().filter { ClaimManager.hasClaimAt(it.location) }.forEach {
            val claim = ClaimManager.getClaimAt(it.location)!!
            if (!claim.getFlags().explosions) {
                e.blockList().remove(it)
            }
        }
    }
    @EventHandler
    fun onBlockExplode(e: BlockExplodeEvent) {
        e.blockList().filter { ClaimManager.hasClaimAt(it.location) }.forEach {
            val claim = ClaimManager.getClaimAt(it.location)!!
            if (!claim.getFlags().explosions) {
                e.blockList().remove(it)
            }
        }
    }

    @EventHandler
    fun onFireSpread(e: BlockSpreadEvent) {
        val claim = ClaimManager.getClaimAt(e.block.location)
        if (claim != null) {
            if (!claim.getFlags().firespread) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntitySpawn(e: EntitySpawnEvent) {
        val claim = ClaimManager.getClaimAt(e.location)
        if (claim != null) {
            if (e.entity is Monster && !claim.getFlags().monsters) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntityDamageEvent(e: EntityDamageByEntityEvent) {
        val claim = ClaimManager.getClaimAt(e.entity.location)
        if (claim != null) {
            if (e.entity is Player && e.damager is Player) {
                if (!claim.getFlags().pvp) {
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onBlockGrow(e: BlockGrowEvent) {
        val claim = ClaimManager.getClaimAt(e.block.location)
        if (claim != null) {
            if (!claim.getFlags().plantGrowth) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onItemPickup(e: EntityPickupItemEvent) {
        val claim = ClaimManager.getClaimAt(e.entity.location)
        if (claim != null) {
            if (e.entity is Player) {
                if (!claim.canBuild(e.entity as Player) && !claim.getFlags().itemPickup) {
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onItemDrop(e: EntityDropItemEvent) {
        val claim = ClaimManager.getClaimAt(e.entity.location)
        if (claim != null) {
            if (e.entity is Player) {
                if (!claim.canBuild(e.entity as Player) && !claim.getFlags().itemDrop) {
                    e.isCancelled = true
                }
            }
        }
    }
}