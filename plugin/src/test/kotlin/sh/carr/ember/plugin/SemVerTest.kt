package sh.carr.ember.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import sh.carr.ember.Version

class SemVerTest :
    FunSpec({
        context("parse") {
            test("parses major.minor.patch") {
                SemVer.parse("1.2.3") shouldBe SemVer(1, 2, 3)
            }

            test("parses with SNAPSHOT pre-release") {
                SemVer.parse("0.1.0-SNAPSHOT") shouldBe SemVer(0, 1, 0, "SNAPSHOT")
            }

            test("parses with rc.N pre-release") {
                SemVer.parse("1.0.0-rc.1") shouldBe SemVer(1, 0, 0, "rc.1")
            }

            test("rejects missing patch component") {
                shouldThrow<IllegalArgumentException> { SemVer.parse("1.2") }
            }

            test("rejects v-prefix") {
                shouldThrow<IllegalArgumentException> { SemVer.parse("v1.2.3") }
            }

            test("rejects leading zero in component") {
                shouldThrow<IllegalArgumentException> { SemVer.parse("01.2.3") }
            }

            test("rejects trailing hyphen with empty pre-release") {
                shouldThrow<IllegalArgumentException> { SemVer.parse("1.2.3-") }
            }

            test("rejects build metadata suffix") {
                shouldThrow<IllegalArgumentException> { SemVer.parse("1.2.3+build.7") }
            }
        }

        context("compareTo") {
            test("orders by major") {
                (SemVer(1, 0, 0) < SemVer(2, 0, 0)) shouldBe true
            }

            test("orders by minor when major equal") {
                (SemVer(1, 1, 0) < SemVer(1, 2, 0)) shouldBe true
            }

            test("orders by patch when major and minor equal") {
                (SemVer(1, 0, 1) < SemVer(1, 0, 2)) shouldBe true
            }

            test("no pre-release outranks pre-release at same triple") {
                (SemVer(1, 0, 0, "rc.1") < SemVer(1, 0, 0)) shouldBe true
            }

            test("numeric pre-release identifier ranks below non-numeric") {
                (SemVer(1, 0, 0, "1") < SemVer(1, 0, 0, "alpha")) shouldBe true
            }

            test("numeric pre-release identifiers compare numerically, not lexically") {
                (SemVer(1, 0, 0, "rc.2") < SemVer(1, 0, 0, "rc.10")) shouldBe true
            }

            test("longer pre-release outranks shorter on otherwise-equal prefix") {
                (SemVer(1, 0, 0, "rc") < SemVer(1, 0, 0, "rc.1")) shouldBe true
            }

            test("equal versions compare equal") {
                SemVer(1, 2, 3).compareTo(SemVer(1, 2, 3)) shouldBe 0
                SemVer(1, 2, 3, "rc.1").compareTo(SemVer(1, 2, 3, "rc.1")) shouldBe 0
            }

            test("falls back to lexical toString comparison against non-SemVer Version") {
                val other =
                    object : Version {
                        override fun toString(): String = "9.9.9"

                        override fun compareTo(other: Version): Int = toString().compareTo(other.toString())
                    }
                (SemVer(1, 0, 0).compareTo(other) < 0) shouldBe true
            }

            test("pre-release is outranked by no-pre-release (reverse direction)") {
                (SemVer(1, 0, 0) > SemVer(1, 0, 0, "rc.1")) shouldBe true
            }

            test("non-numeric pre-release identifier outranks numeric (reverse direction)") {
                (SemVer(1, 0, 0, "alpha") > SemVer(1, 0, 0, "1")) shouldBe true
            }
        }

        context("toString") {
            test("renders triple without pre-release") {
                SemVer(1, 2, 3).toString() shouldBe "1.2.3"
            }

            test("renders triple with pre-release") {
                SemVer(0, 1, 0, "SNAPSHOT").toString() shouldBe "0.1.0-SNAPSHOT"
            }
        }

        context("preRelease accessor") {
            test("returns the constructor-supplied pre-release identifier") {
                SemVer(1, 2, 3, "rc.1").preRelease shouldBe "rc.1"
            }

            test("defaults to null when omitted") {
                SemVer(1, 2, 3).preRelease shouldBe null
            }
        }

        context("round-trip property") {
            test("parse(toString(v)) == v for random major.minor.patch triples") {
                checkAll(
                    Arb.int(0..1000),
                    Arb.int(0..1000),
                    Arb.int(0..1000),
                ) { major, minor, patch ->
                    val v = SemVer(major, minor, patch)
                    SemVer.parse(v.toString()) shouldBe v
                }
            }
        }
    })
