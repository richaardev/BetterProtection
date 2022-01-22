package me.richaardev.betterprotection.managers

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.dao.User
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.transactions.transaction

object UserManager {
    fun getUser(player: OfflinePlayer): User {
        return transaction(BetterProtection.db) {
            return@transaction User.findById(player.uniqueId) ?: User.new(player.uniqueId) {}
        }
    }
}