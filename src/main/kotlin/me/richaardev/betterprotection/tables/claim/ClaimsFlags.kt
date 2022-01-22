package me.richaardev.betterprotection.tables.claim

import org.jetbrains.exposed.dao.id.IntIdTable

object ClaimsFlags : IntIdTable() {

    val allow_explosions = bool("allow_explosions").default(false)
    val allow_firespread = bool("allow_firespread").default(true)
    val allow_monsters = bool("allow_monsters").default(true)
    val allow_pvp = bool("allow_pvp").default(false)
    val allow_plant_growth = bool("allow_plant_growth").default(true)
    val allow_item_drop = bool("allow_item_drop").default(false)
    val allow_item_pickup = bool("allow_item_pickup").default(false)
    val allow_open_doors = bool("allow_open_doors").default(true)

    /*
     Mais TODO:
        - Evitar canhoes de tnt
        - Evitar agua e lava entrar dentro da claim
     */
}