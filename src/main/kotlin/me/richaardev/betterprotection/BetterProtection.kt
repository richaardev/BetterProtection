package me.richaardev.betterprotection


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
import me.richaardev.helper.extensions.plugin.KotlinPlugin
import me.richaardev.helper.menu.HelperMenuListener
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class BetterProtection : KotlinPlugin() {
    companion object {
        lateinit var INSTANCE: BetterProtection
        lateinit var db: org.jetbrains.exposed.sql.Database
        lateinit var conf: BetterProtectionConfiguration

        var ignoringClaims = mutableListOf<UUID>()
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
                TerrainCommand,
                ClaimListCommand,
                AbandonClaimCommand,
                IgnoreClaimsCommand,
                TrustCommand,
                UnTrustCommand,
                TrustListCommand,
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

        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

}