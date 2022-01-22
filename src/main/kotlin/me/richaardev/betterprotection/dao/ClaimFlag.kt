package me.richaardev.betterprotection.dao

import me.richaardev.betterprotection.tables.claim.ClaimsFlags
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ClaimFlag(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ClaimFlag>(ClaimsFlags)

    var explosions by ClaimsFlags.allow_explosions
    var firespread by ClaimsFlags.allow_firespread
    var monsters by ClaimsFlags.allow_monsters
    var pvp by ClaimsFlags.allow_pvp
    var plantGrowth by ClaimsFlags.allow_plant_growth
    var itemDrop by ClaimsFlags.allow_item_drop
    var itemPickup by ClaimsFlags.allow_item_pickup
    var openDoors by ClaimsFlags.allow_open_doors
}