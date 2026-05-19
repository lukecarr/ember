package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import sh.carr.ember.flag.FlagManager
import sh.carr.ember.plugin.flag.Flags

private fun captureMessages(
    source: CommandSourceStack,
    sender: CommandSender,
): MutableList<String> {
    val captured = mutableListOf<String>()
    every { source.sender } returns sender
    every { sender.sendMessage(any<Component>()) } answers {
        captured.add(PlainTextComponentSerializer.plainText().serialize(firstArg()))
    }
    return captured
}

class FlagsCommandTest :
    FunSpec({
        context("node structure") {
            test("literal is 'flags'") {
                val mgr = mockk<FlagManager>()
                FlagsCommand.node(mgr).build().literal shouldBe "flags"
            }

            test("registers 'list' and 'get' children") {
                val mgr = mockk<FlagManager>()
                val built = FlagsCommand.node(mgr).build()
                built.getChild("list").shouldNotBeNull()
                built.getChild("get").shouldNotBeNull()
            }
        }

        context("requires predicate") {
            test("list and get both gate on Permissions.FLAGS") {
                val mgr = mockk<FlagManager>()
                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>()
                every { source.sender } returns sender
                val granted = slot<String>()
                every { sender.hasPermission(capture(granted)) } returns true

                val built = FlagsCommand.node(mgr).build()
                built.getChild("list").requirement.test(source) shouldBe true
                granted.captured shouldBe Permissions.FLAGS

                built.getChild("get").requirement.test(source) shouldBe true
                granted.captured shouldBe Permissions.FLAGS
            }
        }

        context("list") {
            test("emits a header plus one line per registered flag") {
                val mgr = mockk<FlagManager>()
                every { mgr.isEnabled(any()) } returns true

                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>()
                every { sender.hasPermission(Permissions.FLAGS) } returns true
                val messages = captureMessages(source, sender)

                val dispatcher = CommandDispatcher<CommandSourceStack>()
                dispatcher.register(FlagsCommand.node(mgr))
                dispatcher.execute("flags list", source) shouldBe Command.SINGLE_SUCCESS

                messages.size shouldBe 1 + Flags.entries.size
                messages[0] shouldBe "Flags:"
                Flags.entries.forEachIndexed { i, flag ->
                    val line = messages[i + 1]
                    line.contains(flag.id) shouldBe true
                    line.contains("[enabled]") shouldBe true
                    line.contains(flag.description) shouldBe true
                }
            }

            test("renders disabled flags with the disabled badge") {
                val mgr = mockk<FlagManager>()
                every { mgr.isEnabled(any()) } returns false

                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>()
                every { sender.hasPermission(Permissions.FLAGS) } returns true
                val messages = captureMessages(source, sender)

                val dispatcher = CommandDispatcher<CommandSourceStack>()
                dispatcher.register(FlagsCommand.node(mgr))
                dispatcher.execute("flags list", source) shouldBe Command.SINGLE_SUCCESS

                messages.drop(1).forEach { it.contains("[disabled]") shouldBe true }
            }
        }

        context("get") {
            test("default-on flag with isSet=false renders State: enabled and Operator: unset") {
                val mgr = mockk<FlagManager>()
                every { mgr.isSet(Flags.VersionCommand) } returns false
                every { mgr.isEnabled(Flags.VersionCommand) } returns true

                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>()
                every { sender.hasPermission(Permissions.FLAGS) } returns true
                val messages = captureMessages(source, sender)

                val dispatcher = CommandDispatcher<CommandSourceStack>()
                dispatcher.register(FlagsCommand.node(mgr))
                dispatcher.execute("flags get command.version", source) shouldBe Command.SINGLE_SUCCESS

                val joined = messages.joinToString("\n")
                joined.contains("command.version") shouldBe true
                joined.contains(Flags.VersionCommand.description) shouldBe true
                joined.contains("Default: enabled") shouldBe true
                joined.contains("Operator: unset") shouldBe true
                joined.contains("State: enabled") shouldBe true
            }

            test("default-on flag with isSet=true renders State: disabled and Operator: set") {
                val mgr = mockk<FlagManager>()
                every { mgr.isSet(Flags.VersionCommand) } returns true
                every { mgr.isEnabled(Flags.VersionCommand) } returns false

                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>()
                every { sender.hasPermission(Permissions.FLAGS) } returns true
                val messages = captureMessages(source, sender)

                val dispatcher = CommandDispatcher<CommandSourceStack>()
                dispatcher.register(FlagsCommand.node(mgr))
                dispatcher.execute("flags get command.version", source) shouldBe Command.SINGLE_SUCCESS

                val joined = messages.joinToString("\n")
                joined.contains("Default: enabled") shouldBe true
                joined.contains("Operator: set") shouldBe true
                joined.contains("State: disabled") shouldBe true
            }

            // The default-off branch of the Default: line isn't exercisable today because the only
            // catalog entry (Flags.VersionCommand) is default-on. When a default-off flag is added
            // to Flags, an analogous test against that flag closes the gap.
        }
    })
