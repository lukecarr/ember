package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.minimessage.MiniMessage
import sh.carr.ember.Ember
import sh.carr.ember.plugin.flag.Flags

object VersionCommand : Subcommand {
    override val flag = Flags.VersionCommand

    val component by lazy {
        MiniMessage.miniMessage().deserialize("<white>Ember version: <gray>${Ember.instance.version}")
    }

    override fun node(): LiteralArgumentBuilder<CommandSourceStack> =
        Commands
            .literal("version")
            .requires { it.sender.hasPermission(Permissions.VERSION) }
            .executes { ctx ->
                ctx.source.sender.sendMessage(component)
                Command.SINGLE_SUCCESS
            }
}
