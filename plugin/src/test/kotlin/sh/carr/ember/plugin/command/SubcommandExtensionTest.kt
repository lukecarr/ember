package sh.carr.ember.plugin.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import io.mockk.mockk
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import sh.carr.ember.flag.Flag
import sh.carr.ember.flag.FlagManager

private val defaultOnFlag = Flag(id = "test/default-on", description = "test")
private val defaultOffFlag = Flag(id = "test/default-off", description = "test", enabledByDefault = false)

private object UnflaggedSubcommand : Subcommand {
    override fun node(): LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("unflagged")
}

private object DefaultOnSubcommand : Subcommand {
    override val flag = defaultOnFlag

    override fun node(): LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("default-on")
}

private object DefaultOffSubcommand : Subcommand {
    override val flag = defaultOffFlag

    override fun node(): LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("default-off")
}

private fun root() = Commands.literal("root")

class SubcommandExtensionTest :
    FunSpec({
        test("registers a subcommand without a flag") {
            val mgr = mockk<FlagManager>()
            val node = root().subcommand(UnflaggedSubcommand, mgr).build()
            node.getChild("unflagged").shouldNotBeNull()
        }

        test("registers a flagged subcommand when the feature is enabled") {
            val mgr = mockk<FlagManager>()
            every { mgr.isEnabled(any()) } returns true
            val node = root().subcommand(DefaultOnSubcommand, mgr).build()
            node.getChild("default-on").shouldNotBeNull()
        }

        test("skips a flagged subcommand when the feature is disabled") {
            val mgr = mockk<FlagManager>()
            every { mgr.isEnabled(any()) } returns false
            val node = root().subcommand(DefaultOnSubcommand, mgr).build()
            node.getChild("default-on").shouldBeNull()
        }

        test("skips a default-off subcommand when the operator has not set the flag") {
            val mgr = mockk<FlagManager>()
            every { mgr.isEnabled(any()) } returns false
            val node = root().subcommand(DefaultOffSubcommand, mgr).build()
            node.getChild("default-off").shouldBeNull()
        }

        test("registers a default-off subcommand when the operator has set the flag") {
            val mgr = mockk<FlagManager>()
            every { mgr.isEnabled(any()) } returns true
            val node = root().subcommand(DefaultOffSubcommand, mgr).build()
            node.getChild("default-off").shouldNotBeNull()
        }
    })
