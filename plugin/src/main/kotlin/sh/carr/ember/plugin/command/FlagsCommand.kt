package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.minimessage.MiniMessage
import sh.carr.ember.Ember
import sh.carr.ember.flag.Flag
import sh.carr.ember.flag.FlagManager
import sh.carr.ember.plugin.command.argument.FlagArgumentType.Companion.flag
import sh.carr.ember.plugin.flag.Flags

object FlagsCommand : Subcommand {
    private val mm = MiniMessage.miniMessage()

    override fun node(): LiteralArgumentBuilder<CommandSourceStack> = node(Ember.instance.flagManager)

    /** Builds the node against an explicit [flagManager]. Useful for tests. */
    fun node(flagManager: FlagManager): LiteralArgumentBuilder<CommandSourceStack> =
        Commands
            .literal("flags")
            .then(
                Commands
                    .literal("list")
                    .requires { it.sender.hasPermission(Permissions.FLAGS) }
                    .executes { ctx ->
                        listFlags(ctx.source, flagManager)
                        Command.SINGLE_SUCCESS
                    },
            ).then(
                Commands
                    .literal("get")
                    .requires { it.sender.hasPermission(Permissions.FLAGS) }
                    .then(
                        Commands
                            .argument("flag", flag())
                            .executes { ctx ->
                                getFlag(ctx.source, flagManager, ctx.getArgument("flag", Flag::class.java))
                                Command.SINGLE_SUCCESS
                            },
                    ),
            )

    private fun listFlags(
        source: CommandSourceStack,
        flagManager: FlagManager,
    ) {
        source.sender.sendMessage(mm.deserialize("<white>Flags:</white>"))
        Flags.entries.forEach { flag ->
            val badge = if (flagManager.isEnabled(flag)) "<green>[enabled]</green>" else "<red>[disabled]</red>"
            source.sender.sendMessage(
                mm.deserialize("  $badge <white>${flag.id}</white> <gray>— ${flag.description}</gray>"),
            )
        }
    }

    private fun getFlag(
        source: CommandSourceStack,
        flagManager: FlagManager,
        flag: Flag,
    ) {
        val defaultLabel = if (flag.enabledByDefault) "enabled" else "disabled"
        val operatorLabel = if (flagManager.isSet(flag)) "set" else "unset"
        val enabled = flagManager.isEnabled(flag)
        val stateColor = if (enabled) "green" else "red"
        val stateLabel = if (enabled) "enabled" else "disabled"

        source.sender.sendMessage(mm.deserialize("<white>${flag.id}</white>"))
        source.sender.sendMessage(mm.deserialize("  <gray>Description:</gray> ${flag.description}"))
        source.sender.sendMessage(mm.deserialize("  <gray>Default:</gray> $defaultLabel"))
        source.sender.sendMessage(mm.deserialize("  <gray>Operator:</gray> $operatorLabel"))
        source.sender.sendMessage(mm.deserialize("  <gray>State:</gray> <$stateColor>$stateLabel</$stateColor>"))
    }
}
