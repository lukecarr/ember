package sh.carr.ember.plugin.command

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class EmberCommandTest :
    FunSpec({
        test("root literal is 'ember'") {
            EmberCommand.node().literal shouldBe "ember"
        }

        test("registers 'version' as a child") {
            EmberCommand.node().getChild("version").shouldNotBeNull()
        }
    })
