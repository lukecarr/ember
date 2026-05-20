package sh.carr.ember.plugin.flag

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import sh.carr.ember.flag.Flag

private fun testFlag(
    id: String,
    enabledByDefault: Boolean = true,
) = Flag(id = id, description = "test", enabledByDefault = enabledByDefault)

class SimpleFlagManagerTest :
    FunSpec({
        context("isSet") {
            test("returns false when the flag id has not been loaded") {
                val mgr = SimpleFlagManager()
                mgr.isSet(testFlag("unknown")) shouldBe false
            }

            test("returns true when the flag id has been loaded") {
                val mgr = SimpleFlagManager()
                mgr.load(listOf("loaded"))
                mgr.isSet(testFlag("loaded")) shouldBe true
            }

            test("is case-sensitive against the stored ids") {
                val mgr = SimpleFlagManager()
                mgr.load(listOf("loaded"))
                mgr.isSet(testFlag("LOADED")) shouldBe false
            }
        }

        context("load") {
            test("accepts multiple ids in a single call") {
                val mgr = SimpleFlagManager()
                mgr.load(listOf("a", "b"))
                mgr.isSet(testFlag("a")) shouldBe true
                mgr.isSet(testFlag("b")) shouldBe true
            }

            test("is additive across calls") {
                val mgr = SimpleFlagManager()
                mgr.load(listOf("a"))
                mgr.load(listOf("b"))
                mgr.isSet(testFlag("a")) shouldBe true
                mgr.isSet(testFlag("b")) shouldBe true
            }

            test("deduplicates repeated ids") {
                val mgr = SimpleFlagManager()
                mgr.load(listOf("a", "a"))
                mgr.isSet(testFlag("a")) shouldBe true
            }
        }

        context("loadFromFile") {
            test("loads each non-blank line from the file") {
                val mgr = SimpleFlagManager()
                val file =
                    kotlin.io.path
                        .createTempFile("flags", ".txt")
                        .toFile()
                file.deleteOnExit()
                file.writeText("command.version\n")
                mgr.loadFromFile(file)
                mgr.isSet(testFlag("command.version")) shouldBe true
            }

            test("lowercases and trims lines before matching") {
                val mgr = SimpleFlagManager()
                val file =
                    kotlin.io.path
                        .createTempFile("flags", ".txt")
                        .toFile()
                file.deleteOnExit()
                file.writeText("  Command.Version  \n")
                mgr.loadFromFile(file)
                mgr.isSet(testFlag("command.version")) shouldBe true
            }

            test("is a no-op when the file does not exist") {
                val mgr = SimpleFlagManager()
                val file = java.io.File("/this/path/does/not/exist/${java.util.UUID.randomUUID()}")
                mgr.loadFromFile(file)
                mgr.isSet(testFlag("anything")) shouldBe false
            }
        }

        context("inherited isEnabled wiring") {
            test("default-on flag resolves to enabled when nothing is loaded") {
                val mgr = SimpleFlagManager()
                mgr.isEnabled(testFlag("x", enabledByDefault = true)) shouldBe true
            }

            test("default-on flag resolves to disabled once loaded") {
                val mgr = SimpleFlagManager()
                mgr.load(listOf("x"))
                mgr.isEnabled(testFlag("x", enabledByDefault = true)) shouldBe false
            }
        }
    })
