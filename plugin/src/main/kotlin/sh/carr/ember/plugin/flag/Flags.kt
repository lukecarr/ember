package sh.carr.ember.plugin.flag

import sh.carr.ember.flag.Flag

/** Brigadier's unquoted-string charset, which constrains what ids can flow through `FlagArgumentType`. */
private val VALID_FLAG_ID = Regex("[A-Za-z0-9_+.-]+")

/**
 * Enforces the charset constraint that lets a flag id be tab-completed and parsed via
 * `FlagArgumentType` without quoting. Throws [IllegalArgumentException] for ids that would never
 * survive the argument type's native parser.
 */
internal fun requireValidFlagId(id: String) {
    require(VALID_FLAG_ID.matches(id)) {
        "Flag id '$id' must match [A-Za-z0-9_+.-] (Brigadier's unquoted-string charset)."
    }
}

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

    /**
     * All flags defined in this catalog, in source declaration order.
     *
     * Snapshotted once on first access. The catalog is only mutated during object initialization
     * (each `val X = flag(...)` declaration appends to [_entries] in source order), so a lazy
     * snapshot is safe and avoids reallocating on every read.
     */
    val entries: List<Flag> by lazy { _entries.toList() }

    /** Looks up a flag by its [Flag.id], or returns null if no flag with that id is defined. */
    fun byId(id: String): Flag? = _entries.firstOrNull { it.id == id }

    private fun flag(
        id: String,
        description: String,
        enabledByDefault: Boolean = true,
    ): Flag {
        requireValidFlagId(id)
        return Flag(id, description, enabledByDefault).also { _entries.add(it) }
    }

    val VersionCommand =
        flag(
            id = "command.version",
            description = "The /ember version subcommand, which reports the running plugin version.",
        )
}
