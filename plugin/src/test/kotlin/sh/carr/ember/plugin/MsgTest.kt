package sh.carr.ember.plugin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender

private val plain = PlainTextComponentSerializer.plainText()

class MsgTest :
    FunSpec({
        beforeTest { Msg.invalidateAll() }

        context("of(input)") {
            test("returns a Component with the deserialized visible text") {
                plain.serialize(Msg.of("<red>hello")) shouldBe "hello"
            }

            test("returns the same cached instance on repeat calls with the same input") {
                val key = "msg-test-of-cache-${java.util.UUID.randomUUID()}"
                val first = Msg.of(key)
                val second = Msg.of(key)
                second shouldBeSameInstanceAs first
            }

            test("returns distinct instances for distinct inputs") {
                Msg.of("msg-test-distinct-a") shouldNotBeSameInstanceAs Msg.of("msg-test-distinct-b")
            }

            test("records a hit on the static cache after the first call") {
                val key = "msg-test-stats-${java.util.UUID.randomUUID()}"
                val before = Msg.staticStats().hitCount()
                Msg.of(key)
                Msg.of(key)
                (Msg.staticStats().hitCount() - before) shouldBe 1L
            }
        }

        context("of(input, resolvers)") {
            test("substitutes the resolver values into the output") {
                val out =
                    Msg.of(
                        "Hello <name>",
                        Placeholder.unparsed("name", "world"),
                    )
                plain.serialize(out) shouldBe "Hello world"
            }

            test("does not share cached state with of(input)") {
                val key = "msg-test-uncached-${java.util.UUID.randomUUID()}"
                val staticBefore = Msg.staticStats().missCount()
                Msg.of(key, Placeholder.unparsed("ignored", "x"))
                // The static cache should not have been touched.
                Msg.staticStats().missCount() shouldBe staticBefore
            }
        }

        context("ofKeyed") {
            test("caches the result under the supplied cache key") {
                val key = "msg-test-keyed-cache-${java.util.UUID.randomUUID()}"
                val first =
                    Msg.ofKeyed(key, "Hello <name>", Placeholder.unparsed("name", "alpha"))
                val second =
                    Msg.ofKeyed(key, "Hello <name>", Placeholder.unparsed("name", "beta"))
                // Same key returns the cached component, even with different resolver values.
                second shouldBeSameInstanceAs first
            }

            test("distinct keys give distinct components") {
                val a = "msg-test-keyed-a-${java.util.UUID.randomUUID()}"
                val b = "msg-test-keyed-b-${java.util.UUID.randomUUID()}"
                val first =
                    Msg.ofKeyed(a, "Hello <name>", Placeholder.unparsed("name", "alpha"))
                val second =
                    Msg.ofKeyed(b, "Hello <name>", Placeholder.unparsed("name", "beta"))
                second shouldNotBeSameInstanceAs first
                plain.serialize(first) shouldBe "Hello alpha"
                plain.serialize(second) shouldBe "Hello beta"
            }
        }

        context("ofLines") {
            test("returns one Component per input line, each cached via of") {
                val a = "msg-test-lines-a-${java.util.UUID.randomUUID()}"
                val b = "msg-test-lines-b-${java.util.UUID.randomUUID()}"
                val lines = Msg.ofLines(listOf(a, b))
                lines shouldHaveSize 2
                lines[0] shouldBeSameInstanceAs Msg.of(a)
                lines[1] shouldBeSameInstanceAs Msg.of(b)
            }
        }

        context("invalidate") {
            test("evicts a single static-cache entry") {
                val key = "msg-test-invalidate-${java.util.UUID.randomUUID()}"
                val first = Msg.of(key)
                Msg.invalidate(key)
                val second = Msg.of(key)
                second shouldNotBeSameInstanceAs first
            }

            test("evicts a single keyed-cache entry") {
                val key = "msg-test-keyed-invalidate-${java.util.UUID.randomUUID()}"
                val first = Msg.ofKeyed(key, "x")
                Msg.invalidate(key)
                val second = Msg.ofKeyed(key, "x")
                second shouldNotBeSameInstanceAs first
            }
        }

        context("invalidateAll") {
            test("clears every cached component from both caches") {
                val staticKey = "msg-test-clear-static-${java.util.UUID.randomUUID()}"
                val keyedKey = "msg-test-clear-keyed-${java.util.UUID.randomUUID()}"
                val staticFirst = Msg.of(staticKey)
                val keyedFirst = Msg.ofKeyed(keyedKey, "x")
                Msg.invalidateAll()
                Msg.of(staticKey) shouldNotBeSameInstanceAs staticFirst
                Msg.ofKeyed(keyedKey, "x") shouldNotBeSameInstanceAs keyedFirst
            }
        }

        context("cache stats accessors") {
            test("staticStats and keyedStats return Caffeine CacheStats") {
                Msg.staticStats() shouldNotBe null
                Msg.keyedStats() shouldNotBe null
            }
        }

        context("String.msg() extension") {
            test("returns a Component matching Msg.of for the same input") {
                val key = "msg-test-string-ext-${java.util.UUID.randomUUID()}"
                key.msg() shouldBeSameInstanceAs Msg.of(key)
            }
        }

        context("CommandSender.msg(message) extension") {
            test("sends the Component produced by Msg.of") {
                val sender = mockk<CommandSender>()
                val sent = slot<Component>()
                io.mockk.every { sender.sendMessage(capture(sent)) } returns Unit
                val key = "msg-test-sender-ext-${java.util.UUID.randomUUID()}"
                sender.msg(key)
                verify { sender.sendMessage(any<Component>()) }
                sent.captured shouldBeSameInstanceAs Msg.of(key)
            }
        }
    })
