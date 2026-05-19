package sh.carr.ember.plugin.flag

import sh.carr.ember.flag.Flag

/**
 * The plugin's central catalog of feature flags.
 *
 * Each [Flag] is declared once here and referenced from gating call sites via its typed singleton
 * property (for example `Flags.VersionCommand`). The catalog also drives the in-game `/ember flags`
 * command, which iterates [entries] to list flags and looks up by id via [byId] for mutation.
 *
 * To add a new flag, declare a property below using the private [flag] helper. The helper appends
 * each declaration to [entries] in source order, so no separate registration step is required.
 */
object Flags {
    private val _entries = mutableListOf<Flag>()

    /** All flags defined in this catalog, in source declaration order. */
    val entries: List<Flag> get() = _entries.toList()

    /** Looks up a flag by its [Flag.id], or returns null if no flag with that id is defined. */
    fun byId(id: String): Flag? = _entries.firstOrNull { it.id == id }

    private fun flag(
        id: String,
        description: String,
        enabledByDefault: Boolean = true,
    ): Flag = Flag(id, description, enabledByDefault).also { _entries.add(it) }

    val VersionCommand =
        flag(
            id = "command.version",
            description = "The /ember version subcommand, which reports the running plugin version.",
        )
}
