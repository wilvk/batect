package batect.cli

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.throws
import batect.testutils.withMessage
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object PositionalParameterDefinitionSpec : Spek({
    describe("a positional parameter") {
        describe("creation") {
            on("attempting to create a positional parameter with an uppercase name and description") {
                it("does not throw an exception") {
                    assertThat({ PositionalParameterDefinition("THING", "The thing.", true) }, !throws<Throwable>())
                }
            }

            on("attempting to create a positional parameter with a lowercase name") {
                it("throws an exception") {
                    assertThat({ PositionalParameterDefinition("thing", "The thing.", true) }, throws<IllegalArgumentException>(withMessage("Positional parameter name must be all uppercase.")))
                }
            }

            on("attempting to create a positional parameter with a mixed-case name") {
                it("throws an exception") {
                    assertThat({ PositionalParameterDefinition("thInG", "The thing.", true) }, throws<IllegalArgumentException>(withMessage("Positional parameter name must be all uppercase.")))
                }
            }

            on("attempting to create a positional parameter with an empty name") {
                it("throws an exception") {
                    assertThat({ PositionalParameterDefinition("", "The thing.", true) }, throws<IllegalArgumentException>(withMessage("Positional parameter name must not be empty.")))
                }
            }

            on("attempting to create a positional parameter without a description") {
                it("throws an exception") {
                    assertThat({ PositionalParameterDefinition("THING", "", true) }, throws<IllegalArgumentException>(withMessage("Positional parameter description must not be empty.")))
                }
            }
        }
    }
})