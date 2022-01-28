package me.richaardev.betterprotection.listeners

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.betterprotection.managers.VisualizationManager
import me.richaardev.helper.expansions.asOffline
import me.richaardev.helper.expansions.isMoved
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent

class PlayerListener : Listener {
    val bossBars = mutableMapOf<Int, BossBar>()
    @EventHandler
    fun playerChangeSlotEvent(e: PlayerItemHeldEvent) {
        val item = e.player.inventory.getItem(e.newSlot)
        val protectionToolType = BetterProtection.conf.protectionTool
        if (item?.type == protectionToolType) {
            e.player.sendMessage("§eVocê pode proteger até §a${UserManager.getUser(e.player.asOffline()).remainingBlocks} blocos§e!")
        }
    }
    @EventHandler
    fun playerMoveEvent(e: PlayerMoveEvent) {
        if (!e.isMoved) return;

        val terrain = ClaimManager.getClaimAt(e.to!!)
        val lastTerrain = ClaimManager.getClaimAt(e.from)
        var bar = bossBars[terrain?.id?.value] ?: bossBars[lastTerrain?.id?.value]

        Bukkit.getBossBars().asSequence()
            .filter { it.players.contains(e.player) }
            .filter { it.key.key.startsWith("terrain_bossbar_") }
            .filter { !it.key.key.endsWith(terrain?.id?.value.toString()) }
            .forEach {
                it.removePlayer(e.player)
            }

        if (terrain != null) {
            VisualizationManager.createVisualization(e.player, terrain)
            if (bar == null) {
                bar = Bukkit.createBossBar(
                    NamespacedKey(BetterProtection.INSTANCE, "terrain_bossbar_${terrain.id.value}"),
                    "§eTerreno de §a${terrain.owner.name}",
                    BarColor.YELLOW,
                    BarStyle.SEGMENTED_20
                )
                bar.progress = 1.0
            }
            if (!bar.players.contains(e.player)) bar.addPlayer(e.player)
            bossBars.put(terrain.id.value, bar)
            if (terrain.getFlags().pvp) {

            }
        }



        // Iremos verificar os terrenos proximos
        if (terrain == null && lastTerrain == null) {
            val claim = ClaimManager.claims
                .filter { it.world == e.player.location.world }
                .filter { c -> c.corners.any { Location(c.world, it.x.toDouble(), e.player.location.y, it.z.toDouble()).distance(e.player.location) <= 10 } }
                .firstOrNull()
            if (claim != null) VisualizationManager.createVisualization(e.player, claim)
        }
    }
}