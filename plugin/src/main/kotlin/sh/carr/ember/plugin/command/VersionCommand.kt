package sh.carr.ember.plugin.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.minimessage.MiniMessage
import sh.carr.ember.Ember

object VersionCommand {
    val component by lazy {
        MiniMessage.miniMessage().deserialize("<white>Ember version: <gray>${Ember.instance.version}")
    }

    fun node(): LiteralArgumentBuilder<CommandSourceStack> =
        Commands
            .literal("version")
            .requires { it.sender.hasPermission(Permissions.VERSION) }
            .executes { ctx ->
                ctx.source.sender.sendMessage(component)
                Command.SINGLE_SUCCESS
            }
}
