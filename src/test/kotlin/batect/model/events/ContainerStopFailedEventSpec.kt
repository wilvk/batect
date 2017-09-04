package batect.model.events

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import batect.config.Container
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object ContainerStopFailedEventSpec : Spek({
    describe("a 'container stop failed' event") {
        val container = Container("some-container", "/some-build-dir")
        val event = ContainerStopFailedEvent(container, "Something went wrong")

        on("getting the message to display") {
            it("returns a description of the failure") {
                assertThat(event.messageToDisplay, equalTo("the container 'some-container' couldn't be stopped: Something went wrong"))
            }
        }

        on("toString()") {
            it("returns a human-readable representation of itself") {
                assertThat(event.toString(), equalTo("ContainerStopFailedEvent(container: 'some-container', message: 'Something went wrong')"))
            }
        }
    }
})