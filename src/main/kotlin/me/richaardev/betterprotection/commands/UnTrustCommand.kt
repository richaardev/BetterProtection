package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.CommandArguments
import me.richaardev.helper.command.CommandContext
import me.richaardev.helper.command.HelperCommand
import me.richaardev.helper.expansions.asOffline
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.transactions.transaction

class UnTrustCommand : HelperCommand(listOf("untrust")) {
    override fun execute(context: CommandContext, args: CommandArguments) {
        val player = context.requirePlayer()
        val user = UserManager.getUser(player.asOffline())
        val claim = ClaimManager.getClaimAt(player.location)
            ?: return player.sendMessage("§cFique em cima de uma claim para dar trust para alguem.")

        if (!claim.canManage(player)) return player.sendMessage("§cVocê não pode remover trust nessa claim.")
        val name = args[0] ?: return player.sendMessage("§cVocê precisa indicar o jogador qual deseja dar trust")
        val to = Bukkit.getOfflinePlayer(name)
        if (!to.hasPlayedBefore()) return player.sendMessage("§cEsse não é um jogador valido!")

        val trust = claim.getTrusts().firstOrNull { it._id == to.uniqueId } ?: return player.sendMessage("§cEsse jogador não tem trust nessa claim!")
        transaction(BetterProtection.db) {
            trust.delete()
        }

        player.sendMessage("§aVocê removeu o trust de §e${to.name}§a.")
    }
}