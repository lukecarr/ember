package sh.carr.ember.plugin

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import sh.carr.ember.Ember
import sh.carr.ember.plugin.command.EmberCommand

class EmberPlugin :
    JavaPlugin(),
    Ember {
    override fun onEnable() {
        server.servicesManager.register(Ember::class.java, this, this, ServicePriority.Highest)

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            event.registrar().register(EmberCommand.node())
        }
    }

    override val version = pluginMeta.version
}
