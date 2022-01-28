package me.richaardev.betterprotection.util

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

data class ClaimSelection(
    var player: Player,
    ) {
    var locs = mutableListOf<Border>()

    var pos1: Block? = null
    var pos2: Block? = null
        set(value) {
            field = value
            if (pos1 != null) {
                locs.clear()
                locs.addAll(getBordersBlocks())
            }
        }

    fun getCorners(height: Double = player.location.y): List<Location> {
        if (pos1 == null || pos2 == null)
            return emptyList()

        val minX = Math.min(pos1!!.x, pos2!!.x).toDouble()
        val minZ = Math.min(pos1!!.z, pos2!!.z).toDouble()

        val maxX = Math.max(pos1!!.x, pos2!!.x).toDouble()
        val maxZ = Math.max(pos1!!.z, pos2!!.z).toDouble()

        val corner1 = Location(pos1!!.world, minX, height, minZ).add(0.5, 0.5, 0.5)
        val corner2 = Location(pos1!!.world, maxX, height, maxZ).add(0.5, 0.5, 0.5)
        val corner3 = Location(pos1!!.world, minX, height, maxZ).add(0.5, 0.5, 0.5)
        val corner4 = Location(pos1!!.world, maxX, height, minZ).add(0.5, 0.5, 0.5)

        return listOf(corner1, corner2, corner3, corner4)
    }
    fun getBordersBlocks(): List<Border> {
        val minX = Math.min(pos1!!.x, pos2!!.x)
        val minZ = Math.min(pos1!!.z, pos2!!.z)

        val maxX = Math.max(pos1!!.x, pos2!!.x)
        val maxZ = Math.max(pos1!!.z, pos2!!.z)

        val locs = mutableListOf<Border>()
        for (x in minX..maxX) {
            val location1 = Border(x, minZ)
            val location2 = Border(x, maxZ)
            if (!locs.contains(location1) && !locs.contains(location2)) {
                locs.add(location1)
                locs.add(location2)
            }
        }
        for (z in minZ..maxZ) {
            val location1 = Border(minX,  z)
            val location2 = Border(maxX,  z)
            if (!locs.contains(location1) && !locs.contains(location2)) {
                locs.add(location1)
                locs.add(location2)
            }

        }

        return locs
    }

}