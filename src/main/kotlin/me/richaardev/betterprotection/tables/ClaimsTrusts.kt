package me.richaardev.betterprotection.tables

import me.richaardev.betterprotection.tables.claim.Claims
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import java.util.*

object ClaimsTrusts : IdTable<UUID>() {
    val _id = uuid("id")


    val claim = reference("claim", Claims)

    // Permite o jogador quebrar blocos e colocar blocos
    val build = bool("build").default(true)

    // Permite o jogador acessar os containers
    val container = bool("containers").default(false)

    // Permite o jogador gerenciar a claim, dando trust
    val manage = bool("manage").default(false)

    override val id: Column<EntityID<UUID>> = _id.entityId()
    override val primaryKey = PrimaryKey(id)
}