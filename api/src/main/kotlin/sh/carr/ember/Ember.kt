package sh.carr.ember

import org.bukkit.Bukkit
import sh.carr.ember.flag.FlagManager

interface Ember {
    companion object {
        @JvmStatic
        val instance: Ember =
            Bukkit
                .getServer()
                .servicesManager
                .getRegistration(Ember::class.java)!!
                .provider
    }

    val version: Version

    /** Resolves feature flag state for this plugin instance. */
    val flagManager: FlagManager
}
