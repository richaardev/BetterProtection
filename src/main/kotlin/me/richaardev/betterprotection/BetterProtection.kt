package me.richaardev.betterprotection


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.richaardev.betterprotection.commands.*
import me.richaardev.betterprotection.dao.Claim
import me.richaardev.betterprotection.listeners.ClaimFlagsListener
import me.richaardev.betterprotection.listeners.ClaimListener
import me.richaardev.betterprotection.listeners.PlayerListener
import me.richaardev.betterprotection.listeners.SelectionListener
import me.richaardev.betterprotection.managers.ClaimManager
import me.richaardev.betterprotection.tables.ClaimsTrusts
import me.richaardev.betterprotection.tables.claim.ClaimsFlags
import me.richaardev.betterprotection.tables.claim.Claims
import me.richaardev.betterprotection.tables.Users
import me.richaardev.helper.database.HelperDatabase
import me.richaardev.helper.database.DatabaseType
import me.richaardev.helper.expansions.plugin.KotlinPlugin
import me.richaardev.helper.menu.HelperMenuListener
import me.richaardev.helper.scheduler.schedule
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.SystemColor.text
import java.io.File
import java.io.FileFilter
import java.time.Duration
import java.util.*

class BetterProtection : KotlinPlugin() {
    companion object {
        lateinit var INSTANCE: BetterProtection
        lateinit var db: org.jetbrains.exposed.sql.Database
        lateinit var conf: BetterProtectionConfiguration

        var ignoringClaims = mutableListOf<UUID>()

        fun debug(message: String) {
            INSTANCE.logger.info("[DEBUG] §e$message")
        }
    }

    override fun softEnable() {
        try {
            INSTANCE = this;
            conf = BetterProtectionConfiguration(this)
            db = HelperDatabase.createDataBase(this, DatabaseType.SQLITE)


            registerListeners(
                SelectionListener(),
                PlayerListener(),
                ClaimListener(),
                HelperMenuListener(),
                ClaimFlagsListener()
            )
            registerCommands(
                TerrainCommand(),
                ClaimListCommand(),
                AbandonClaimCommand(),
                IgnoreClaimsCommand(),
                TrustCommand(),
                UnTrustCommand(),
                TrustListCommand(),
                AddBlocksCommand(),
            )

            transaction(db) {
                SchemaUtils.createMissingTablesAndColumns(
                    Claims,
                    Users,
                    ClaimsFlags,
                    ClaimsTrusts
                )
                Claim.all().forEach {
                    ClaimManager.claims.add(it)
                }

                logger.info("Foi carregado um total de ${ClaimManager.claims.size} terrenos")
            }

            startSecurityBackup()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun startSecurityBackup() {
        /*
         * O sistema de backups, irá armazenar somente o dono e o tamanho do terreno
         * Estou fazendo esse sistema por causa estou com bastante medo de perder os terrenos
         * fazendo alguma modificação nos dao ou nas tables
        */

        val backupFolder = File(dataFolder, "backups")
        if (!backupFolder.exists()) {
            backupFolder.mkdir()
        }

        schedule {
            while (true) {
                val json = JsonObject()
                val claims = JsonArray()
                ClaimManager.claims.forEach {
                    val obj = JsonObject()
                    obj.addProperty("id", it.id.value)
                    obj.addProperty("owner", it.owner.uniqueId.toString())
                    obj.addProperty("corner1", it.corner1)
                    obj.addProperty("corner2", it.corner2)
                    obj.addProperty("world", it.world.name)
                    claims.add(obj)
                }
                json.add("claims", claims)

                val lastFile = backupFolder.listFiles()?.maxByOrNull { it.lastModified() }
                val lastFileText = lastFile?.readText()
                if (lastFileText?.trim() == json.toString().trim()) {
                    debug("Não houve mudanças nos arquivos")
                } else {
                    val file = File(backupFolder, "backup_${System.currentTimeMillis()}.json")
                    file.writeText(json.toString())
                    debug("Foram encontrada mudanças, portanto salvando novo backup")
                }

                backupFolder.listFiles()?.filter { it.name.startsWith("backup_") }?.forEach {
                    val toDeleteTime = Duration.ofHours(5).toMillis() // vamos deletar depois de 5 horas
                    if (it.lastModified() + toDeleteTime < System.currentTimeMillis()) {
                        if (backupFolder.listFiles()!!.size <= 10) {
                            debug("Prevenindo a remoção de ${it.name} pois não há backups suficientes")
                            return@forEach
                        }
                        it.delete()
                        debug("Deletado o backup ${it.name}")
                    }
                }
                waitFor(20 * 60 * 5) // 5 minutos
            }
        }
    }
}
