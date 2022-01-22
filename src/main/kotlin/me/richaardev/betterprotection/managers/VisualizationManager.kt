package me.richaardev.betterprotection.managers

import me.richaardev.betterprotection.dao.Claim
import me.richaardev.betterprotection.util.ClaimVisualization
import org.bukkit.entity.Player

object VisualizationManager {
    val visualizations = mutableListOf<ClaimVisualization>()


    fun createVisualization(player: Player, claim: Claim): ClaimVisualization {
        if (visualizations.any { it.player.name == player.name }) {
            val v = visualizations.filter { it.player == player }
            v.forEach {
                if (it.claim == claim) return it
                removeVisualization(it)
            }
        }

        val visu = ClaimVisualization(player, claim)
        visualizations.add(visu)
        return visu
    }

    fun removeVisualization(visualization: ClaimVisualization) = visualizations.remove(visualization)
    fun removeVisualization(claim: Claim) {
        val v = visualizations.filter { it.claim == claim }
        v.forEach { it.destroy() }
    }
}