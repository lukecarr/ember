package sh.carr.ember.flag

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private class FixedFlagManager(
    private val setIds: Set<String>,
) : FlagManager {
    override fun isSet(flag: Flag) = flag.id in setIds
}

private fun testFlag(
    id: String,
    enabledByDefault: Boolean = true,
) = Flag(id = id, description = "test", enabledByDefault = enabledByDefault)

class FlagManagerTest :
    FunSpec({
        context("default isEnabled resolves enabledByDefault XOR isSet") {
            test("default-on, not set: feature is enabled") {
                val mgr = FixedFlagManager(emptySet())
                mgr.isEnabled(testFlag(id = "x", enabledByDefault = true)) shouldBe true
            }

            test("default-on, set: feature is disabled") {
                val mgr = FixedFlagManager(setOf("x"))
                mgr.isEnabled(testFlag(id = "x", enabledByDefault = true)) shouldBe false
            }

            test("default-off, not set: feature is disabled") {
                val mgr = FixedFlagManager(emptySet())
                mgr.isEnabled(testFlag(id = "x", enabledByDefault = false)) shouldBe false
            }

            test("default-off, set: feature is enabled") {
                val mgr = FixedFlagManager(setOf("x"))
                mgr.isEnabled(testFlag(id = "x", enabledByDefault = false)) shouldBe true
            }

            test("only the matching id flips the resolution") {
                val mgr = FixedFlagManager(setOf("other"))
                mgr.isEnabled(testFlag(id = "x", enabledByDefault = true)) shouldBe true
            }
        }
    })
