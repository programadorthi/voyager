package cafe.adriel.voyager.routing

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import dev.programadorthi.routing.core.RouteMethod
import dev.programadorthi.routing.core.StackRouteMethod
import dev.programadorthi.routing.core.application.call
import dev.programadorthi.routing.core.install
import dev.programadorthi.routing.core.pop
import dev.programadorthi.routing.core.push
import dev.programadorthi.routing.core.replace
import dev.programadorthi.routing.core.replaceAll
import dev.programadorthi.routing.core.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
internal class VoyagerIntegrationTest {

    @Test
    fun shouldThrowAnExceptionWhenVoyagerNavigatorPluginIsNotInstalled() {
        executeBody { coroutineContext, composition ->
            // GIVEN
            val router = routing(parentCoroutineContext = coroutineContext) {}
            val result = assertFails {
                composition.setContent {
                    VoyagerRouter(router = router)
                }
            }
            // THEN
            assertIs<IllegalStateException>(result)
            assertEquals("No instance for key AttributeKey: VoyagerNavigatorManager", result.message)
        }
    }

    @Test
    fun shouldNotThrowAnExceptionWhenVoyagerNavigatorPluginIsInstalled() {
        executeBody { coroutineContext, composition ->
            // GIVEN
            val router = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)
            }
            composition.setContent {
                VoyagerRouter(router = router)
            }
            // THEN
        }
    }

    @Test
    fun shouldPushAScreen() {
        val expected = TestScreen(value = "push test")

        executeBody { coroutineContext, composition ->
            // GIVEN
            val router = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)

                screen(path = "/path") {
                    expected
                }
            }

            composition.setContent {
                VoyagerRouter(router = router)
            }

            // WHEN
            router.push(path = "/path")
            advanceTimeBy(99)

            // THEN
            val navigator = router.voyagerNavigatorManager.navigator
            assertNotNull(navigator)
            assertEquals(expected, navigator.lastItem)
        }
    }

    @Test
    fun shouldReplaceAScreen() {
        var event: RouteMethod? = null
        val expected = TestScreen(value = "replace test")

        executeBody { coroutineContext, composition ->
            // GIVEN
            val router = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)

                screen(path = "/path") {
                    TestScreen(value = "push test")
                }

                screen(path = "/path2") {
                    event = call.routeMethod
                    expected
                }
            }

            composition.setContent {
                VoyagerRouter(router = router)
            }

            // WHEN
            router.push(path = "/path")
            advanceTimeBy(99)
            router.replace(path = "/path2")
            advanceTimeBy(99)

            // THEN
            val navigator = router.voyagerNavigatorManager.navigator
            assertNotNull(navigator)
            assertEquals(expected, navigator.lastItem)
            assertEquals(StackRouteMethod.Replace, event)
        }
    }

    @Test
    fun shouldReplaceAllAScreen() {
        var event: RouteMethod? = null
        val expected = TestScreen(value = "replace all test")

        executeBody { coroutineContext, composition ->
            // GIVEN
            val router = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)

                screen(path = "/path") {
                    TestScreen(value = "push test")
                }

                screen(path = "/path2") {
                    event = call.routeMethod
                    expected
                }
            }

            composition.setContent {
                VoyagerRouter(router = router)
            }

            // WHEN
            router.push(path = "/path")
            advanceTimeBy(99)
            router.replaceAll(path = "/path2")
            advanceTimeBy(99)

            // THEN
            val navigator = router.voyagerNavigatorManager.navigator
            assertNotNull(navigator)
            assertEquals(expected, navigator.lastItem)
            assertEquals(StackRouteMethod.ReplaceAll, event)
        }
    }

    @Test
    fun shouldPopAScreen() {
        var event: RouteMethod? = null
        val expected = TestScreen(value = "pushed test 1")

        executeBody { coroutineContext, composition ->
            val job = Job(parent = coroutineContext[Job])
            // GIVEN
            val router = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)

                screen(path = "/path1") {
                    event = call.routeMethod
                    expected
                }

                screen(path = "/path2") {
                    event = call.routeMethod
                    TestScreen(value = "pushed test 2")
                }

                pop(path = "/path2") {
                    event = call.routeMethod
                    job.complete() // A hack to wait receive pop event from Hook
                }
            }

            composition.setContent {
                VoyagerRouter(router = router)
            }

            // WHEN
            router.push(path = "/path1")
            advanceTimeBy(99)
            router.push(path = "/path2")
            advanceTimeBy(99)
            router.pop()
            advanceTimeBy(99)

            // THEN
            val navigator = router.voyagerNavigatorManager.navigator
            assertNotNull(navigator)
            assertEquals(expected, navigator.lastItem)
            assertEquals(StackRouteMethod.Pop, event)
        }
    }

    @Test
    fun shouldHaveLocalNavigationWhenHavingNestedRouting() {
        val expected = TestScreen(value = "push child test")

        executeBody { coroutineContext, composition ->
            // GIVEN
            val parent = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)

                screen(path = "/parent-path") {
                    TestScreen(value = "push parent test")
                }
            }

            val router = routing(
                rootPath = "/child",
                parent = parent,
                parentCoroutineContext = coroutineContext
            ) {
                install(VoyagerNavigator)

                screen(path = "/child-path") {
                    expected
                }
            }

            composition.setContent {
                VoyagerRouter(router = router)
            }

            // WHEN
            router.push(path = "/child-path")
            advanceTimeBy(99)

            // THEN
            val navigator = router.voyagerNavigatorManager.navigator
            assertNotNull(navigator)
            assertEquals(expected, navigator.lastItem)
        }
    }

    @Test
    fun shouldHandleParentRouteWhenRoutingFromChild() {
        val expected = TestScreen(value = "push parent test")

        executeBody { coroutineContext, composition ->
            // GIVEN
            val parent = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)

                screen(path = "/parent-path") {
                    expected
                }
            }

            val router = routing(
                rootPath = "/child",
                parent = parent,
                parentCoroutineContext = coroutineContext
            ) {
                install(VoyagerNavigator)

                screen(path = "/child-path") {
                    TestScreen(value = "push child test")
                }
            }

            composition.setContent {
                VoyagerRouter(router = router)
            }

            // WHEN
            router.push(path = "/parent-path")
            advanceTimeBy(99)

            // THEN
            val navigator = router.voyagerNavigatorManager.navigator
            assertNotNull(navigator)
            assertEquals(expected, navigator.lastItem)
        }
    }

    @Test
    fun shouldHandleChildRouteWhenRoutingFromParent() {
        val expected = TestScreen(value = "push child test")

        executeBody { coroutineContext, composition ->
            // GIVEN
            val parent = routing(parentCoroutineContext = coroutineContext) {
                install(VoyagerNavigator)

                screen(path = "/parent-path") {
                    TestScreen(value = "push parent test")
                }
            }

            val router = routing(
                rootPath = "/child",
                parent = parent,
                parentCoroutineContext = coroutineContext
            ) {
                install(VoyagerNavigator)

                screen(path = "/child-path") {
                    expected
                }
            }

            composition.setContent {
                VoyagerRouter(router = router)
            }

            // WHEN
            parent.push(path = "/child/child-path")
            advanceTimeBy(99)

            // THEN
            val navigator = router.voyagerNavigatorManager.navigator
            assertNotNull(navigator)
            assertEquals(expected, navigator.lastItem)
        }
    }

    private fun executeBody(
        body: TestScope.(CoroutineContext, Composition) -> Unit
    ) = runTest {
        // SETUP
        val job = Job()
        val clock = BroadcastFrameClock()
        val scope = CoroutineScope(coroutineContext + job + clock)
        val recomposer = Recomposer(scope.coroutineContext)
        val runner = scope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
        val composition = Composition(UnitApplier(), recomposer)
        try {
            body(scope.coroutineContext, composition)
        } finally {
            runner.cancel()
            recomposer.close()
            job.cancel()
        }
    }
}
