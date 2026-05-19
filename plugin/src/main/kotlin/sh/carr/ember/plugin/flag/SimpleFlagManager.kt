package sh.carr.ember.plugin.flag

import sh.carr.ember.flag.Flag
import sh.carr.ember.flag.FlagManager

/**
 * In-memory [FlagManager] backed by a [HashSet] of flag identifiers.
 *
 * The plugin populates the set at startup via [load]. The set is not safe for concurrent
 * modification once command registration has run.
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
}
