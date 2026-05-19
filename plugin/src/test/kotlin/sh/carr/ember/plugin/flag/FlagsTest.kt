package sh.carr.ember.plugin.flag

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

class FlagsTest :
    FunSpec({
        context("catalog membership") {
            test("VersionCommand is registered in entries") {
                Flags.entries shouldContain Flags.VersionCommand
            }

            test("every registered flag has a non-empty description") {
                Flags.entries.forEach { it.description.shouldNotBeEmpty() }
            }
        }

        context("byId") {
            test("returns the flag matching a known id") {
                Flags.byId("command.version") shouldBe Flags.VersionCommand
            }

            test("returns null for an unknown id") {
                Flags.byId("does-not-exist").shouldBeNull()
            }
        }
    })
