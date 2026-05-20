package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import sh.carr.ember.Ember
import sh.carr.ember.flag.Flag
import sh.carr.ember.flag.FlagManager
import sh.carr.ember.plugin.command.argument.FlagArgumentType.Companion.flag
import sh.carr.ember.plugin.flag.Flags
import sh.carr.ember.plugin.msg

object FlagsCommand : Subcommand {
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
        source.sender.msg("<white>Flags:</white>")
        Flags.entries.forEach { flag ->
            val badge = if (flagManager.isEnabled(flag)) "<green><b>✔</b></green>" else "<red><b>✘</b></red>"
            source.sender.msg(
                "  $badge <white><hover:show_text:'${flag.description}'>${flag.id}</hover></white>",
            )
        }
    }

    private fun getFlag(
        source: CommandSourceStack,
        flagManager: FlagManager,
        flag: Flag,
    ) {
        val defaultLabel = enabledLabel(flag.enabledByDefault)
        val operatorLabel = if (flagManager.isSet(flag)) "set" else "unset"
        val enabled = flagManager.isEnabled(flag)
        val stateColor = if (enabled) "green" else "red"
        val stateLabel = enabledLabel(enabled)

        source.sender.msg(
            "<white>${flag.id}</white>  <gray>State:</gray> <$stateColor><hover:show_text:'<gray>Default:</gray> $defaultLabel  <gray>Operator:</gray> $operatorLabel'>$stateLabel</hover></$stateColor>",
        )
        source.sender.msg("  <gray>${flag.description}</gray>")
    }
}

/** Maps an enabled/disabled boolean to its user-facing label. */
internal fun enabledLabel(enabled: Boolean): String = if (enabled) "enabled" else "disabled"
