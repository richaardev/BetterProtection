package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.dao.ClaimTrust
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.CommandArguments
import me.richaardev.helper.command.CommandContext
import me.richaardev.helper.command.HelperCommand
import me.richaardev.helper.expansions.asOffline
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.transactions.transaction

class TrustCommand : HelperCommand(listOf("trust", "confiar")) {
    override fun execute(context: CommandContext, args: CommandArguments) {
        val player = context.requirePlayer()
        val user = UserManager.getUser(player.asOffline())
        val claim = ClaimManager.getClaimAt(player.location)
            ?: return player.sendMessage("§cFique em cima de uma claim para dar trust para alguem.")

        if (!claim.canManage(player)) return player.sendMessage("§cVocê não pode dar trust nessa claim.")

        val name = args[0] ?: return player.sendMessage("§cVocê precisa indicar o jogador qual deseja dar trust")
        val to = Bukkit.getOfflinePlayer(name)
//        if (!to.hasPlayedBefore()) return player.sendMessage("§cEsse não é um jogador valido!")


        val trust = transaction(BetterProtection.db) {
            val trust = ClaimTrust.new(to.uniqueId) {
                this.claim = claim.id
            }
            return@transaction trust
        }

        player.sendMessage("§aVocê deu trust para §e${to.name}§a.")

    }
}