package sh.carr.ember

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.bukkit.plugin.ServicePriority
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import sh.carr.ember.flag.Flag
import sh.carr.ember.flag.FlagManager

class EmberTest :
    FunSpec({
        lateinit var server: ServerMock

        beforeTest {
            server = MockBukkit.mock()
        }

        afterTest {
            MockBukkit.unmock()
        }

        test("Ember.instance resolves to the highest-priority registered provider") {
            val fakePlugin = MockBukkit.createMockPlugin()
            val fakeEmber =
                object : Ember {
                    override val version =
                        object : Version {
                            override fun compareTo(other: Version): Int = toString().compareTo(other.toString())

                            override fun toString(): String = "9.9.9"
                        }

                    override val flagManager =
                        object : FlagManager {
                            override fun isSet(flag: Flag) = false
                        }
                }
            server.servicesManager.register(Ember::class.java, fakeEmber, fakePlugin, ServicePriority.Highest)

            Ember.instance shouldBe fakeEmber
            // Also drive the @JvmStatic synthetic forwarder so Java-style callers are covered.
            Ember::class.java.getMethod("getInstance").invoke(null) shouldBe fakeEmber
        }
    })
