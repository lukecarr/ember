package sh.carr.ember.plugin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import sh.carr.ember.Ember

class EmberPluginTest :
    FunSpec({
        lateinit var server: ServerMock
        lateinit var plugin: EmberPlugin

        beforeTest {
            server = MockBukkit.mock()
            plugin = MockBukkit.load(EmberPlugin::class.java)
        }

        afterTest {
            MockBukkit.unmock()
        }

        test("registers itself as the Ember service provider") {
            val registration = server.servicesManager.getRegistration(Ember::class.java)
            registration.shouldNotBeNull()
            registration.provider shouldBe plugin
        }

        test("exposes plugin version as a parsed SemVer") {
            plugin.version shouldBe SemVer(0, 1, 0, "alpha.1")
        }

        test("Ember.instance resolves to the loaded plugin") {
            Ember.instance shouldBe plugin
        }

        test("dispatching a command fires the COMMANDS lifecycle and registers /ember") {
            // dispatchCommand triggers MockBukkit's PaperCommands dispatcher build, which fires
            // LifecycleEvents.COMMANDS and runs onEnable's handler lambda — covering it without
            // depending on internal MockBukkit invocation order.
            server.dispatchCommand(server.consoleSender, "ember version") shouldBe true
        }
    })
