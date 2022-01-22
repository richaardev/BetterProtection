package me.richaardev.betterprotection

import me.richaardev.helper.configuration.BukkitYamlConfiguration
import me.richaardev.helper.configuration.annotation.Comment
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Comment(
    "--------------------------------------------------",
    "       BetterProtection - Configuration",
    "        Created with ❤ by richaardev",
    "--------------------------------------------------",
    "\n\n"
)
class BetterProtectionConfiguration(plugin: JavaPlugin) : BukkitYamlConfiguration(File(plugin.dataFolder, "config.yml")) {

    @delegate:Comment("Mundos onde poderá usar a proteção do terreno")
    var enabledWorlds by list("config.enabled-worlds", listOf("world", "world_nether"))

    @delegate:Comment(
        "Quantidade inicial de blocos do jogador",
        " !! Caso altere isso os jogadores que já entraram não irão ter a quantidade de alterada !!"
    )
    var initialBlocks by int("config.initial-blocks", 300)
    var protectionTool by material("config.protection-tool", Material.GOLDEN_SHOVEL)

}
