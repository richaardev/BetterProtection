package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.CommandArguments
import me.richaardev.helper.command.CommandContext
import me.richaardev.helper.command.HelperCommand
import me.richaardev.helper.expansions.scheduler
import me.richaardev.helper.scheduler.SynchronizationContext
import me.richaardev.helper.scheduler.schedule
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

class AddBlocksCommand : HelperCommand(listOf("addblocks", "ab")) {
    override fun execute(context: CommandContext, args: CommandArguments) {
        val to = args.getOfflinePlayer(0) ?: return context.reply("§cEsse jogador não é um jogador válido!")
        val amount = args.getInt(1) ?: return context.reply("§cEspecifique uma quantidade de blocos válida que deseja dar ao jogador!")

        scheduler().schedule(BetterProtection.INSTANCE, SynchronizationContext.ASYNC) {
            val user = UserManager.getUser(to)
            transaction(BetterProtection.db) {
                user.blocks += amount
            }
            switchContext(SynchronizationContext.SYNC)
            context.reply("§aVocê adicionou §e${amount} §ablocos §aao jogador §e${to.name}§a!")
        }

    }

}