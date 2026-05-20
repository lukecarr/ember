package sh.carr.ember.plugin.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import sh.carr.ember.flag.Flag

/**
 * A subcommand registered under the root `/ember` command.
 *
 * Implementations may override [flag] to gate their registration on an operator-controlled feature
 * flag. Subcommands that should always be available leave [flag] null.
 */
interface Subcommand {
    /** The flag that controls whether this subcommand is registered, or null if it always is. */
    val flag: Flag? get() = null

    /** Returns the Brigadier node for this subcommand. */
    fun node(): LiteralArgumentBuilder<CommandSourceStack>
}
