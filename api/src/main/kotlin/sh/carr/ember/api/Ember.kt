package sh.carr.ember.api

import org.bukkit.Bukkit

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

    val version: String
}
