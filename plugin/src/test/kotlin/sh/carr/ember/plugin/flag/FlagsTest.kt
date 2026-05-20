package sh.carr.ember.plugin.flag

import io.kotest.assertions.throwables.shouldThrow
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

        context("requireValidFlagId") {
            test("accepts ids made up of letters, digits, and Brigadier's symbol set") {
                listOf("a", "command.version", "foo-bar_baz+1", "0").forEach { requireValidFlagId(it) }
            }

            test("rejects an id containing a forward slash") {
                shouldThrow<IllegalArgumentException> { requireValidFlagId("foo/bar") }
            }

            test("rejects an empty id") {
                shouldThrow<IllegalArgumentException> { requireValidFlagId("") }
            }

            test("rejects an id with whitespace") {
                shouldThrow<IllegalArgumentException> { requireValidFlagId("foo bar") }
            }

            test("every registered catalog id passes the check") {
                Flags.entries.forEach { requireValidFlagId(it.id) }
            }
        }
    })
