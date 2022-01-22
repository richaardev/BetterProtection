package me.richaardev.betterprotection.dao

import me.richaardev.betterprotection.tables.ClaimsTrusts
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class ClaimTrust(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ClaimTrust>(ClaimsTrusts)

    var _id by ClaimsTrusts._id

    var claim by ClaimsTrusts.claim

    var build by ClaimsTrusts.build

    var container by ClaimsTrusts.container

    var manage by ClaimsTrusts.manage
}