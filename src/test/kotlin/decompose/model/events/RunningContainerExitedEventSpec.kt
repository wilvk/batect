package decompose.model.events

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import decompose.config.Container
import decompose.docker.DockerContainer
import decompose.model.steps.RemoveContainerStep
import decompose.model.steps.StopContainerStep
import decompose.testutils.withMessage
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object RunningContainerExitedEventSpec : Spek({
    describe("a 'running container exited' event") {
        val dependency1 = Container("dependency-container-1", "/dependency-container-1-build-dir")
        val dependency2 = Container("dependency-container-2", "/dependency-container-2-build-dir")

        val container = Container("container-1", "/container-1-build-dir", dependencies = setOf(dependency1.name, dependency2.name))
        val event = RunningContainerExitedEvent(container, 123)

        describe("being applied") {
            on("when the container that exited was the task container") {
                val dockerContainer = DockerContainer("container-1-container", "container-1")
                val dependency1DockerContainer = DockerContainer("dependency-container-1-container", "dependency-container-1")
                val dependency2DockerContainer = DockerContainer("dependency-container-2-container", "dependency-container-2")

                val context = mock<TaskEventContext> {
                    on { isTaskContainer(container) } doReturn true
                    on { getPastEventsOfType<ContainerCreatedEvent>() } doReturn setOf(
                            ContainerCreatedEvent(container, dockerContainer),
                            ContainerCreatedEvent(dependency1, dependency1DockerContainer),
                            ContainerCreatedEvent(dependency2, dependency2DockerContainer)
                    )
                    on { dependenciesOf(container) } doReturn setOf(dependency1, dependency2)
                }

                event.apply(context)

                it("queues a 'remove container' step for the container that exited") {
                    verify(context).queueStep(RemoveContainerStep(container, dockerContainer))
                }

                it("queues a 'stop container' step for all dependencies of the container that exited") {
                    verify(context).queueStep(StopContainerStep(dependency1, dependency1DockerContainer))
                    verify(context).queueStep(StopContainerStep(dependency2, dependency2DockerContainer))
                }
            }

            on("when the container that exited was not the task container") {
                val context = mock<TaskEventContext> {
                    on { isTaskContainer(container) } doReturn false
                }

                it("throws an exception") {
                    assertThat({ event.apply(context) }, throws<IllegalArgumentException>(withMessage("The container 'container-1' is not the task container.")))
                }
            }
        }

        on("toString()") {
            it("returns a human-readable representation of itself") {
                assertThat(event.toString(), equalTo("RunningContainerExitedEvent(container: 'container-1', exit code: 123)"))
            }
        }
    }
})
