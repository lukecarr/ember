package sh.carr.ember.plugin.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import sh.carr.ember.Ember
import sh.carr.ember.flag.FlagManager

object EmberCommand {
    fun node(): LiteralCommandNode<CommandSourceStack> =
        Commands
            .literal("ember")
            .subcommand(VersionCommand)
            .subcommand(FlagsCommand)
            .build()
}

/**
 * Registers [command] as a child of this builder unless its [Subcommand.flag] resolves to disabled
 * in [flagManager].
 *
 * Subcommands with a null [Subcommand.flag] are always registered. Tests may pass an explicit
 * [flagManager] to avoid touching the live [Ember] service.
 */
fun LiteralArgumentBuilder<CommandSourceStack>.subcommand(
    command: Subcommand,
    flagManager: FlagManager = Ember.instance.flagManager,
): LiteralArgumentBuilder<CommandSourceStack> {
    val flag = command.flag
    if (flag != null && !flagManager.isEnabled(flag)) {
        return this
    }
    return then(command.node())
}
