package decompose.journeytests

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object ListTasksJourneyTest : Spek({
    given("a configuration file with multiple tasks") {
        val runner = ApplicationRunner("many-tasks", listOf("tasks", "decompose.yml"))

        on("listing available tasks") {
            val result = runner.run()

            it("prints a list of all available tasks") {
                assert.that(result.output, containsSubstring("task-1\ntask-2\ntask-3\n"))
            }

            it("returns a zero exit code") {
                assert.that(result.exitCode, equalTo(0))
            }
        }
    }
})