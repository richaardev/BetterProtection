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

object TrustCommand : DSLCommandBase<BetterProtection> {
    override fun command(plugin: BetterProtection) = create(listOf("trust")) {
        executes {
            val user = UserManager.getUser(player.asOffline())
            val claim = ClaimManager.getClaimAt(player.location)
                ?: return@executes player.sendMessage("§cFique em cima de uma claim para dar trust para alguem.")

            if (!claim.canManage(player)) fail("Você não pode dar trust nessa claim.")

            val name = args.getOrNull(0) ?: fail("Você precisa indicar o jogador qual deseja dar trust")
            val to = Bukkit.getOfflinePlayer(name)
            if (!to.hasPlayedBefore()) fail("Esse não é um jogador valido!")


            val trust = transaction(BetterProtection.db) {
                val trust = ClaimTrust.new(to.uniqueId) {
                    this.claim = claim.id
                }
                return@transaction trust
            }

            player.sendMessage("§aVocê deu trust para §e${to.name}§a.")
        }
    }
}