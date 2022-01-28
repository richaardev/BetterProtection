package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.helper.command.CommandArguments
import me.richaardev.helper.command.CommandContext
import me.richaardev.helper.command.HelperCommand

class IgnoreClaimsCommand : HelperCommand(listOf("ignoreclaims")) {
    override val permission = "betterprotection.ignoreclaims"

    override fun execute(context: CommandContext, args: CommandArguments) {
        val player = context.requirePlayer()

        if (BetterProtection.ignoringClaims.contains(player.uniqueId)) {
            BetterProtection.ignoringClaims.remove(player.uniqueId)
            player.sendMessage("§aVocê agora está respeitando as claims.")
        } else {
            BetterProtection.ignoringClaims.add(player.uniqueId)
            player.sendMessage("§aVocê agora está ignorando as claims.")
        }
    }
}