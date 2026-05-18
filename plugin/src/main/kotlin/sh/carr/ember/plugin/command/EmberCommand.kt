package sh.carr.ember.plugin.command

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands

object EmberCommand {
    fun node(): LiteralCommandNode<CommandSourceStack> =
        Commands
            .literal("ember")
            .then(VersionCommand.node())
            .build()
}
