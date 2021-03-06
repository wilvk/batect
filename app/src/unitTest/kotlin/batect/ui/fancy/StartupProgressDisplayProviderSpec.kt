/*
   Copyright 2017-2019 Charles Korn.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package batect.ui.fancy

import batect.config.Container
import batect.execution.ContainerDependencyGraph
import batect.execution.ContainerDependencyGraphNode
import batect.testutils.imageSourceDoesNotMatter
import batect.testutils.on
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartupProgressDisplayProviderSpec : Spek({
    describe("a startup progress display provider") {
        val provider = StartupProgressDisplayProvider()

        fun createNodeFor(container: Container, dependencies: Set<Container>): ContainerDependencyGraphNode {
            return mock<ContainerDependencyGraphNode> {
                on { this.container } doReturn container
                on { dependsOnContainers } doReturn dependencies
            }
        }

        on("creating a progress display for a dependency graph") {
            val container1 = Container("container-1", imageSourceDoesNotMatter())
            val container2 = Container("container-2", imageSourceDoesNotMatter())

            val container1Dependencies = setOf(container2)
            val container2Dependencies = emptySet<Container>()

            val node1 = createNodeFor(container1, container1Dependencies)
            val node2 = createNodeFor(container2, container2Dependencies)

            val graph = mock<ContainerDependencyGraph> {
                on { allNodes } doReturn setOf(node1, node2)
            }

            val display = provider.createForDependencyGraph(graph)
            val linesForContainers = display.containerLines.associateBy { it.container }

            it("returns progress lines for each node in the graph") {
                assertThat(display.containerLines.map { it.container }.toSet(),
                    equalTo(setOf(container1, container2)))
            }

            it("returns a progress line for the first node with its dependencies") {
                assertThat(linesForContainers[container1]!!.dependencies, equalTo(container1Dependencies))
            }

            it("returns a progress line for the second node with its dependencies") {
                assertThat(linesForContainers[container2]!!.dependencies, equalTo(container2Dependencies))
            }
        }
    }
})
