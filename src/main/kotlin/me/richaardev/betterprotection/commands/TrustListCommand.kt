package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.CommandArguments
import me.richaardev.helper.command.CommandContext
import me.richaardev.helper.command.HelperCommand
import me.richaardev.helper.expansions.asOffline
import org.bukkit.Bukkit

class TrustListCommand : HelperCommand(listOf("trustlist")) {
    override fun execute(context: CommandContext, args: CommandArguments) {
        val player = context.requirePlayer()
        val user = UserManager.getUser(player.asOffline())
        val claim = ClaimManager.getClaimAt(player.location)
            ?: return player.sendMessage("§cFique em cima de uma claim para ver a lista de trust.")


        val trust = claim.getTrusts()
        player.sendMessage("§aJogadores que tem trust nessa claim: §b${trust.map { Bukkit.getOfflinePlayer(it._id).name }.joinToString(", ")}")
    }
}