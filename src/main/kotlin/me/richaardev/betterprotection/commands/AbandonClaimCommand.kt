package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.DSLCommandBase
import me.richaardev.helper.extensions.asOffline

object AbandonClaimCommand : DSLCommandBase<BetterProtection> {
    override fun command(plugin: BetterProtection) = create(listOf("unclaim")) {
        executes {
            val claim = ClaimManager.getClaimAt(player.location)
                ?: return@executes player.sendMessage("§cFique em cima de uma claim para abandoná-la.")
            if (claim.owner.uniqueId != player.uniqueId && !player.hasPermission("betterprotection.admin")) return@executes player.sendMessage("§cVocê não é o dono desta claim.")


            ClaimManager.removeClaim(claim)
            player.sendMessage("§aClaim abandonada com sucesso.")
            player.sendMessage("§aAgora você tem §b${UserManager.getUser(player.asOffline()).remainingBlocks} blocos§a!")
        }
    }
}