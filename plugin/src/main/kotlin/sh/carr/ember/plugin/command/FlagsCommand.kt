package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import sh.carr.ember.Ember
import sh.carr.ember.flag.Flag
import sh.carr.ember.flag.FlagManager
import sh.carr.ember.plugin.command.argument.FlagArgumentType.Companion.flag
import sh.carr.ember.plugin.flag.Flags
import sh.carr.ember.plugin.msg
import sh.carr.ember.plugin.msgKeyed

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
            val enabled = flagManager.isEnabled(flag)
            val badge = if (enabled) "<green><b>✔</b></green>" else "<red><b>✘</b></red>"
            // Cache key encodes every variable affecting the rendered component: the flag id
            // (which determines flag.description, immutable per flag) and the enabled state
            // (which selects the badge). The static catalog plus binary state means at most
            // 2 cache entries per flag, reused across every /ember flags list invocation.
            source.sender.msgKeyed(
                "flags.list.${flag.id}.${enabledLabel(enabled)}",
                "  $badge <white><hover:show_text:'<description>'><id></hover></white>",
                Placeholder.unparsed("id", flag.id),
                Placeholder.unparsed("description", flag.description),
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

        // flag.enabledByDefault is immutable per flag, so defaultLabel is determined by id alone.
        // enabled is determined by (default XOR isSet), which collapses to isSet for cache-key
        // purposes once id is fixed. So (id, isSet) captures every variable in this rendering.
        source.sender.msgKeyed(
            "flags.get.line.${flag.id}.$operatorLabel",
            "<white><id></white>  <gray>State:</gray> <$stateColor>" +
                "<hover:show_text:'<gray>Default:</gray> $defaultLabel  <gray>Operator:</gray> $operatorLabel'>" +
                "$stateLabel</hover></$stateColor>",
            Placeholder.unparsed("id", flag.id),
        )
        // Description line is purely per-flag; never varies with state.
        source.sender.msgKeyed(
            "flags.get.description.${flag.id}",
            "  <gray><description></gray>",
            Placeholder.unparsed("description", flag.description),
        )
    }
}

/** Maps an enabled/disabled boolean to its user-facing label. */
internal fun enabledLabel(enabled: Boolean): String = if (enabled) "enabled" else "disabled"
