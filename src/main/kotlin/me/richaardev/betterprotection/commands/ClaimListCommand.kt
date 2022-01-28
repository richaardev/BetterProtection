package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.CommandArguments
import me.richaardev.helper.command.CommandContext
import me.richaardev.helper.command.HelperCommand
import me.richaardev.helper.expansions.asOffline
import me.richaardev.helper.expansions.toTextComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent

class ClaimListCommand : HelperCommand(listOf("claimlist", "listclaims", "listclaim")) {
    override fun execute(context: CommandContext, args: CommandArguments) {
        val player = context.requirePlayer()
        val claims = ClaimManager.getClaimsFrom(player.asOffline())
        val user = UserManager.getUser(player.asOffline())

        player.sendMessage("§aSua lista de claims ${claims.size} (${user.remainingBlocks}/${user.blocks} blocos): ")
        claims.forEachIndexed { index, claim ->
            val component = " §a${index+1}- §7${claim.world.name} §7X: §f${claim.center.x} §7Z: §f${claim.center.z}  §7(§f${claim.area}§7 blocos)".toTextComponent()
            if (player.hasPermission("betterprotection.claim.teleport")) component.apply {
                hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("§aClique para se teletransportar")))
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp ${player.name} ${claim.center.x} ${claim.world.getHighestBlockAt(claim.center.x, claim.center.z)} ${claim.center.z}")
            }

            player.sendMessage(component)
        }
    }
}