package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.CommandArguments
import me.richaardev.helper.command.CommandContext
import me.richaardev.helper.command.HelperCommand
import me.richaardev.helper.expansions.asOffline

class AbandonClaimCommand : HelperCommand(listOf("abandonclaim", "unclaim")) {

    override fun execute(context: CommandContext, args: CommandArguments) {
        val player = context.requirePlayer()
        val claim = ClaimManager.getClaimAt(player.location)
            ?: return player.sendMessage("§cFique em cima de uma claim para abandoná-la.")
        if (claim.owner.uniqueId != player.uniqueId && !player.hasPermission("betterprotection.admin")) return player.sendMessage("§cVocê não é o dono desta claim.")


        ClaimManager.removeClaim(claim)
        player.sendMessage("§aClaim abandonada com sucesso.")
        player.sendMessage("§aAgora você tem §b${UserManager.getUser(player.asOffline()).remainingBlocks} blocos§a!")

    }
}