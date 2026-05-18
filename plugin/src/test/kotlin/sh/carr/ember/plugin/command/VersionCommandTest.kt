package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import sh.carr.ember.plugin.EmberPlugin

class VersionCommandTest :
    FunSpec({
        context("node structure") {
            test("literal is 'version'") {
                VersionCommand.node().build().literal shouldBe "version"
            }
        }

        context("requires predicate") {
            test("returns true when sender has the permission") {
                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>()
                every { source.sender } returns sender
                every { sender.hasPermission(Permissions.VERSION) } returns true

                VersionCommand
                    .node()
                    .build()
                    .requirement
                    .test(source) shouldBe true
            }

            test("returns false when sender lacks the permission") {
                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>()
                every { source.sender } returns sender
                every { sender.hasPermission(Permissions.VERSION) } returns false

                VersionCommand
                    .node()
                    .build()
                    .requirement
                    .test(source) shouldBe false
            }
        }

        context("executes") {
            lateinit var server: ServerMock

            beforeTest {
                server = MockBukkit.mock()
                MockBukkit.load(EmberPlugin::class.java)
            }

            afterTest {
                MockBukkit.unmock()
            }

            test("dispatches the version message to the sender") {
                val source = mockk<CommandSourceStack>()
                val sender = mockk<CommandSender>(relaxed = true)
                every { source.sender } returns sender
                every { sender.hasPermission(Permissions.VERSION) } returns true

                val dispatcher = CommandDispatcher<CommandSourceStack>()
                dispatcher.register(VersionCommand.node())

                val result = dispatcher.execute("version", source)

                result shouldBe Command.SINGLE_SUCCESS
                verify { sender.sendMessage(any<Component>()) }
            }
        }
    })
