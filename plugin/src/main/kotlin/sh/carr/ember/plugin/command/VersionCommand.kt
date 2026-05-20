package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import sh.carr.ember.Ember
import sh.carr.ember.plugin.flag.Flags
import sh.carr.ember.plugin.msg

object VersionCommand : Subcommand {
    override val flag = Flags.VersionCommand

    override fun node(): LiteralArgumentBuilder<CommandSourceStack> =
        Commands
            .literal("version")
            .requires { it.sender.hasPermission(Permissions.VERSION) }
            .executes { ctx ->
                ctx.source.sender.msg("<white>Ember version: <gray>${Ember.instance.version}")
                Command.SINGLE_SUCCESS
            }
}
