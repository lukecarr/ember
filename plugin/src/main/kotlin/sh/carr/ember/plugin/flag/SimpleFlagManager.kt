package sh.carr.ember.plugin.flag

import sh.carr.ember.flag.Flag
import sh.carr.ember.flag.FlagManager
import java.io.File

/**
 * In-memory [FlagManager] backed by a [HashSet] of flag identifiers.
 *
 * The plugin populates the set at startup via [load] or [loadFromFile]. No further modification is
 * permitted after the plugin has registered itself as a service: that publication establishes the
 * happens-before edge that subsequent reads from arbitrary threads rely on. Reads alone against a
 * [HashSet] are safe once that edge exists; later writes would invalidate it.
 */
class SimpleFlagManager : FlagManager {
    private val flags = HashSet<String>()

    override fun isSet(flag: Flag) = flags.contains(flag.id)

    /**
     * Adds [ids] to the operator's configured flag list.
     */
    fun load(ids: Iterable<String>) {
        flags.addAll(ids)
    }

    /**
     * Loads flag ids from [file] if it exists. Lines are lowercased and trimmed before they're
     * matched. A missing file is a no-op.
     *
     * Blank lines or lines prefixed with `#` (comments) are ignored.
     */
    fun loadFromFile(file: File) {
        if (file.exists()) {
            load(
                file
                    .readLines()
                    .map { it.lowercase().trim() }
                    .filter { it.isNotBlank() && !it.startsWith("#") },
            )
        }
    }
}
