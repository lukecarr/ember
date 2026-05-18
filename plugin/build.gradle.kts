import sh.carr.ember.gradle.GeneratePermissionConstantsTask
import sh.carr.ember.gradle.GeneratePluginLoaderTask
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    id("ember.common-conventions")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
    id("com.gradleup.shadow") version "9.4.1"
}

base {
    archivesName = "ember"
}

val description: String by project
val paperApiVersion: String by project
val kotlinVersion: String by project

dependencies {
    implementation(project(":api"))
    paperweight.paperDevBundle(paperApiVersion)
}

paperPluginYaml {
    name = "Ember"
    main = "sh.carr.ember.plugin.EmberPlugin"
    website = "https://ember.carr.sh/"
    apiVersion = "26.1.2"
    author = "Luke Carr"
    loader = "sh.carr.ember.plugin.EmberPluginLoader"
    foliaSupported = true

    permissions {
        register("ember.version") {
            description.set("Check the running Ember version")
            default.set(Permission.Default.OP)
        }
    }
}

val generatePermissionConstants =
    tasks.register<GeneratePermissionConstantsTask>("generatePermissionConstants") {
        permissionNames.set(provider { paperPluginYaml.permissions.names.toList() })
        permissionDescriptions.set(
            provider {
                paperPluginYaml.permissions
                    .mapNotNull { p -> p.description.orNull?.let { p.name to it } }
                    .toMap()
            },
        )
        permissionDefaults.set(
            provider {
                paperPluginYaml.permissions
                    .mapNotNull { p -> p.default.orNull?.let { p.name to it.serialized } }
                    .toMap()
            },
        )
        packageName.set("sh.carr.ember.plugin.command")
        commonPrefix.set("ember.")
        outputDir.set(layout.buildDirectory.dir("generated/sources/permissions/main/kotlin"))
    }

val generatePluginLoader =
    tasks.register<GeneratePluginLoaderTask>("generatePluginLoader") {
        packageName.set("sh.carr.ember.plugin")
        className.set("EmberPluginLoader")
        kotlinStdlibVersion.set(kotlinVersion)
        outputDir.set(layout.buildDirectory.dir("generated/sources/loader/main/java"))
    }

kotlin {
    sourceSets["main"].kotlin.srcDir(generatePermissionConstants)
}

sourceSets["main"].java.srcDir(generatePluginLoader)

ktlint {
    filter {
        exclude { it.file.path.contains("/generated/sources/permissions/") }
    }
}

tasks {
    shadowJar {
        archiveClassifier = ""
        mergeServiceFiles()

        dependencies {
            include(project(":api"))
        }
    }

    jar {
        archiveClassifier = "noshade"
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("26.1.2")
    }
}
