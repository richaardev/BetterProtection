import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.richaardev.betterprotection"
version = "22.01-SNAPSHOT"

bukkit {
    name = "BetterProtection"
    main = "me.richaardev.betterprotection.BetterProtection"
    apiVersion = "1.18"
    author = "richaardev"
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")

    api("me.richaardev.helper:helper:1.0.2-SNAPSHOT") // from local repository

    api("com.zaxxer:HikariCP:5.0.0")
    api("org.xerial:sqlite-jdbc:3.30.1")
    api("org.jetbrains.exposed:exposed-core:0.35.3")
    api("org.jetbrains.exposed:exposed-jdbc:0.35.3")
    api("org.jetbrains.exposed:exposed-dao:0.35.3")
    api("com.github.ben-manes.caffeine:caffeine:3.0.5")
}

tasks {
    val shadowJar = named<ShadowJar>("shadowJar") {
        dependencies {
            include(dependency("me.richaardev.helper:.*:.*"))
        }
    }

    generateBukkitPluginDescription {
        doFirst {
            val dependencies =
                project.configurations.getByName("api").allDependencies//.resolvedConfiguration.firstLevelModuleDependencies
                    .asSequence()
                    .filter { it.name != "helper" }
                    .map {
                        "${it.group}:${it.name}:${it.version}"
                    }.toList()

            (this@generateBukkitPluginDescription.pluginDescription.get() as net.minecrell.pluginyml.bukkit.BukkitPluginDescription).libraries = dependencies
        }
    }

    "build" {
        dependsOn(shadowJar)
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
