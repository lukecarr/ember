import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    jacoco
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

jacoco {
    toolVersion = "0.8.14"
}

dependencies {
    "testImplementation"("io.kotest:kotest-runner-junit5:6.1.11")
    "testImplementation"("io.kotest:kotest-assertions-core:6.1.11")
    "testImplementation"("io.kotest:kotest-property:6.1.11")
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release = 25
        options.encoding = Charsets.UTF_8.name()
    }
    withType<Javadoc>().configureEach {
        options.encoding = Charsets.UTF_8.name()
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }
    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required = true
            html.required = true
        }
    }
    jacocoTestCoverageVerification {
        dependsOn(jacocoTestReport)
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }
    check {
        dependsOn(jacocoTestCoverageVerification)
    }
}
