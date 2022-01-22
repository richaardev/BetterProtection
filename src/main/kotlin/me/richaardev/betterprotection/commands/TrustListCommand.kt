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

object TrustListCommand : DSLCommandBase<BetterProtection> {
    override fun command(plugin: BetterProtection) = create(listOf("trustlist")) {
        executes {
            val user = UserManager.getUser(player.asOffline())
            val claim = ClaimManager.getClaimAt(player.location)
                ?: return@executes player.sendMessage("§cFique em cima de uma claim para ver a lista de trust.")


            val trust = claim.getTrusts()
            player.sendMessage("§aJogadores que tem trust nessa claim: §b${trust.map { Bukkit.getOfflinePlayer(it._id).name }.joinToString(", ")}")
        }
    }
}