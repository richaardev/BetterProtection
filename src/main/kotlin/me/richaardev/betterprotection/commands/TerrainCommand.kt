package me.richaardev.betterprotection.commands

import com.google.gson.Gson
import me.richaardev.betterprotection.BetterProtection
import me.richaardev.betterprotection.dao.Claim
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.managers.UserManager
import me.richaardev.betterprotection.tables.claim.Claims.owner
import me.richaardev.helper.command.DSLCommandBase
import me.richaardev.helper.extensions.asOffline
import me.richaardev.helper.extensions.playSound
import me.richaardev.helper.menu.createMenu
import me.richaardev.helper.utils.ItemUtils.glow
import me.richaardev.helper.utils.ItemUtils.lore
import me.richaardev.helper.utils.ItemUtils.meta
import me.richaardev.helper.utils.ItemUtils.rename
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream
import java.net.URL


object TerrainCommand : DSLCommandBase<BetterProtection> {
    override fun command(plugin: BetterProtection) = create(listOf("terrain", "terreno", "claim")) {
        executes {
            val userTerrains = ClaimManager.getClaimsFrom(player.asOffline())
            val user = UserManager.getUser(player.asOffline())
            val claim = ClaimManager.getClaimAt(player.location)


            val menu = createMenu("Menu de terrenos", 3) {
                slot(1, 5) {
                    item = ItemStack(Material.PLAYER_HEAD)
                        .meta<SkullMeta> {
                            this.owner = player.name
                        }
                        .rename("§a§lSuas informações")
                        .lore(
                            "",
                            "§bSeus blocos: §a${user.blocks}",
                            "§bBlocos em uso: §a${user.blocks - user.remainingBlocks}",
                            "",
                            "§bTerrenos ativo\ns: §a${userTerrains.size}"
                        )
                }

                slot (2, 3) {
                    item = ItemStack(Material.GRASS_BLOCK)
                        .rename("§aAlterar as configurações")
                        .lore(
                            "",
                            "§bAltere as configurações da sua claim atual",
                            "§bdando trust e etc..."
                        )
                    if (claim != null && claim.canManage(player)) item = item.glow()
                    onClick {
                        if (claim == null) return@onClick player.sendMessage("§cVocê não está em nenhuma claim!")
                        if (!claim.canManage(player)) return@onClick player.sendMessage("§cVocê não pode gerenciar essa claim!")

                        terrainConfiguration(player, claim)
                    }
                }

                slot(3, 1) {
                    item = ItemStack(Material.TOTEM_OF_UNDYING)
                        .rename("§e§l${BetterProtection.INSTANCE.name} Plugin")
                        .lore(
                            "",
                            "",
                            "§6Created with §c❤ §6by §brichaardev"
                        )
                }
            }.sendTo(player)
        }
    }

    fun defaultMenu(player: Player) {
        player.performCommand("terrain")
    }
    fun terrainConfiguration(player: Player, claim: Claim) {
        val menu = createMenu("Configurações do terreno", 3) {
            slot(2, 5) {
                item = ItemStack(Material.RED_BANNER)
                    .rename("§a§lFlags do Terreno")
                onClick {
                    flagsConfiguration(player, claim)
                }
            }
            slot (2, 7) {
                item = ItemStack(Material.PLAYER_HEAD)
                    .rename("§a§lJogadores confiavéis")
                onClick {
                    trustedUsers(player, claim)
                }
            }
            slot(3, 1) {
                item = ItemStack(Material.ARROW)
                    .rename("§cVoltar")
                onClick { _ ->
                    defaultMenu(player)
                }
            }
        }
        menu.sendTo(player)
    }

    fun trustedUsers(player: Player, claim: Claim) {
        val menu = createMenu("Trusted Users", 6) {
            claim.getTrusts().chunked(7).forEachIndexed { index, trusts ->
                if (trusts.isNotEmpty()) {
                    trusts.forEachIndexed { i, trust ->
                        slot(index + 2, i + 2) {
                            item = ItemStack(Material.PLAYER_HEAD)
                                .rename("§a${Bukkit.getOfflinePlayer(trust._id).name}")
                        }
                    }
                }
            }

            slot(6, 1) {
                item = ItemStack(Material.ARROW)
                    .rename("§cVoltar")
                onClick { _ ->
                    terrainConfiguration(player, claim)
                }
            }
        }
        menu.sendTo(player)
    }

    fun flagsConfiguration(player: Player, claim: Claim) {
        fun getName(isEnabled: Boolean): String {
            return if (isEnabled) "§a§lAtivado" else "§c§lDesativado"
        }

        val menu = createMenu("Alterar as flags do Terreno", 6) {
            slot(2, 2) {
                val isEnabled = claim.getFlags().explosions
                item = ItemStack(Material.TNT)
                    .rename("§aExplosões do terreno")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().explosions = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }
            slot(2, 3) {
                val isEnabled = claim.getFlags().firespread
                item = ItemStack(Material.CAMPFIRE)
                    .rename("§aPropagação de fogo")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().firespread = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }
            slot(2, 4) {
                val isEnabled = claim.getFlags().monsters
                item = ItemStack(Material.ZOMBIE_SPAWN_EGG)
                    .rename("§aSpawn de monstros")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().monsters = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }
            slot(2, 5) {
                val isEnabled = claim.getFlags().pvp
                item = ItemStack(Material.DIAMOND_SWORD)
                    .rename("§aPvP")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().pvp = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }
            slot(2, 6) {
                val isEnabled = claim.getFlags().plantGrowth
                item = ItemStack(Material.WHEAT_SEEDS)
                    .rename("§aPlantas germinando")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().plantGrowth = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }
            slot(2, 7) {
                val isEnabled = claim.getFlags().itemDrop
                item = ItemStack(Material.DIRT)
                    .rename("§aDrop de itens")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().itemDrop = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }
            slot(2, 8) {
                val isEnabled = claim.getFlags().itemPickup
                item = ItemStack(Material.DIRT)
                    .rename("§aColheta de itens")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().itemPickup = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }
            slot(3, 2) {
                val isEnabled = claim.getFlags().openDoors
                item = ItemStack(Material.DARK_OAK_DOOR)
                    .rename("§aAbertura de portas ou trapdoors")
                    .lore(
                        "",
                        "§bEstado atual: §a${getName(isEnabled)}",
                        "",
                        "§bClique para alterar"
                    )
                if (isEnabled) item.glow()
                onClick {
                    transaction(BetterProtection.db) {
                        claim.getFlags().openDoors = !isEnabled
                        player.playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        flagsConfiguration(player, claim)
                    }
                }
            }


            slot(6, 1) {
                item = ItemStack(Material.ARROW)
                    .rename("§cVoltar")
                onClick { _ ->
                    terrainConfiguration(player, claim)
                }
            }

        }
        menu.sendTo(player)
    }
}