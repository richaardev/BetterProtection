package me.richaardev.betterprotection.commands

import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.dao.Claim
import me.richaardev.betterprotection.dao.ClaimTrust
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.helper.command.DSLCommandBase
import me.richaardev.helper.menu.createMenu
import me.richaardev.helper.utils.ItemUtils.lore
import me.richaardev.helper.utils.ItemUtils.rename
import me.richaardev.helper.extensions.asOffline
import me.richaardev.helper.extensions.playSound
import me.richaardev.helper.utils.ItemUtils.glow
import me.richaardev.helper.utils.ItemUtils.meta
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.jetbrains.exposed.sql.transactions.transaction

object UnTrustCommand : DSLCommandBase<BetterProtection> {
    override fun command(plugin: BetterProtection) = create(listOf("untrust")) {
        executes {
            val user = UserManager.getUser(player.asOffline())
            val claim = ClaimManager.getClaimAt(player.location)
                ?: return@executes player.sendMessage("§cFique em cima de uma claim para dar trust para alguem.")

            if (!claim.canManage(player)) fail("Você não pode remover trust nessa claim.")
            val name = args.getOrNull(0) ?: fail("Você precisa indicar o jogador qual deseja dar trust")
            val to = Bukkit.getOfflinePlayer(name)
            if (!to.hasPlayedBefore()) fail("Esse não é um jogador valido!")

            val trust = claim.getTrusts().firstOrNull { it._id == to.uniqueId } ?: fail("Esse jogador não tem trust nessa claim!")
            transaction(BetterProtection.db) {
                trust.delete()
            }

            player.sendMessage("§aVocê removeu o trust de §e${to.name}§a.")
        }
    }
}