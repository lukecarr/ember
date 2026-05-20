package sh.carr.ember.flag

/**
 * A feature flag that can be toggled by the server operator.
 *
 * Flags are declared once in a central catalog and referenced by typed singleton properties at
 * call sites. The operator supplies a list of flag ids in configuration; the [FlagManager] checks
 * that list against each flag's [enabledByDefault] to decide whether the gated feature is active.
 *
 * @property id Identifier matched against the operator's configured flag list.
 * @property description Operator-facing prose rendered by the in-game flags command. One short
 *   sentence is the right shape.
 * @property enabledByDefault When true, the feature is active unless the operator sets the flag.
 *   When false, the feature is inactive unless the operator sets the flag.
 */
data class Flag(
    val id: String,
    val description: String,
    val enabledByDefault: Boolean = true,
)
