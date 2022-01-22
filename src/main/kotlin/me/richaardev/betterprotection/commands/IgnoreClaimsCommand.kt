package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.helper.command.DSLCommandBase

object IgnoreClaimsCommand : DSLCommandBase<BetterProtection> {
    override fun command(plugin: BetterProtection) = create(listOf("ignoreclaims")) {
        permission = "betterprotection.ignoreclaims"
        executes {
            if (BetterProtection.ignoringClaims.contains(player.uniqueId)) {
                BetterProtection.ignoringClaims.remove(player.uniqueId)
                player.sendMessage("§aVocê agora está respeitando as claims.")
            } else {
                BetterProtection.ignoringClaims.add(player.uniqueId)
                player.sendMessage("§aVocê agora está ignorando as claims.")
            }
        }
    }
}