import java.util.Properties

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

val rootProperties = Properties()
file("../gradle.properties").reader().use { rootProperties.load(it) }
val kotlinVersion = rootProperties.getProperty("kotlinVersion")
    ?: error("kotlinVersion missing from root gradle.properties")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:14.2.0")
}
