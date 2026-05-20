package sh.carr.ember.flag

/**
 * Resolves feature flag state for the running plugin.
 *
 * The operator supplies a list of flag identifiers in configuration. Each [Flag] declares its own
 * default state via [Flag.enabledByDefault], so a configured flag may either disable a default-on
 * feature or enable a default-off one.
 */
interface FlagManager {
    /**
     * Returns true if the operator has set this flag in configuration.
     *
     * This is the raw storage check. Most callers want [isEnabled], which resolves the flag
     * against its declared default.
     */
    fun isSet(flag: Flag): Boolean

    /**
     * Returns true if the feature guarded by this flag is currently active.
     *
     * Combines [Flag.enabledByDefault] with whether the operator has set the flag.
     */
    fun isEnabled(flag: Flag): Boolean = flag.enabledByDefault xor isSet(flag)
}
