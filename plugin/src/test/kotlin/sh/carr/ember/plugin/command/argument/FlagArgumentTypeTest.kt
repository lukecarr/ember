package sh.carr.ember.plugin.command.argument

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.mockk
import sh.carr.ember.plugin.command.argument.FlagArgumentType.Companion.flag
import sh.carr.ember.plugin.flag.Flags

class FlagArgumentTypeTest :
    FunSpec({
        context("convert") {
            test("resolves a known flag id to its Flag instance") {
                flag().convert("command.version") shouldBe Flags.VersionCommand
            }

            test("throws a CommandSyntaxException for an unknown id") {
                shouldThrow<CommandSyntaxException> { flag().convert("does-not-exist") }
            }

            test("error message includes the offending id") {
                val ex = shouldThrow<CommandSyntaxException> { flag().convert("bogus-id") }
                (ex.message ?: "") shouldContain "bogus-id"
            }
        }

        context("getNativeType") {
            test("returns a word-style string argument") {
                flag().nativeType.javaClass.simpleName shouldBe "StringArgumentType"
            }
        }

        context("listSuggestions") {
            test("offers every registered flag id") {
                val ctx = mockk<CommandContext<Any>>()
                val builder = SuggestionsBuilder("", 0)
                flag().listSuggestions(ctx, builder).join()
                builder
                    .build()
                    .list
                    .map { it.text } shouldContainAll Flags.entries.map { it.id }
            }
        }

        context("getExamples") {
            test("returns a non-empty sample of catalog ids") {
                flag().examples shouldNotBe emptyList<String>()
                Flags.entries.map { it.id } shouldContainAll flag().examples
            }
        }
    })
