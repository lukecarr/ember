package sh.carr.ember.plugin

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import sh.carr.ember.Ember
import sh.carr.ember.plugin.command.EmberCommand
import sh.carr.ember.plugin.flag.SimpleFlagManager
import java.io.File

open class EmberPlugin :
    JavaPlugin(),
    Ember {
    override fun onEnable() {
        server.servicesManager.register(Ember::class.java, this, this, ServicePriority.Highest)

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            event.registrar().register(EmberCommand.node())
        }

        flagManager.loadFromFile(File(dataFolder, "flags.txt"))
    }

    override val version = SemVer.parse(pluginMeta.version)

    override val flagManager = SimpleFlagManager()
}
