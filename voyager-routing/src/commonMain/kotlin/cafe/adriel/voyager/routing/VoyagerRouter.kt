package cafe.adriel.voyager.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import dev.programadorthi.routing.core.Routing
import dev.programadorthi.routing.core.asRouting

@Composable
public fun VoyagerRouter(
    router: Routing,
    initialScreen: Screen = VoyagerEmptyScreen(),
) {
    CompositionLocalProvider(VoyagerLocalRouter provides router) {
        Navigator(initialScreen) { navigator ->
            SideEffect {
                router.linkCurrentNavigatorToParents(navigator)
            }

            CurrentScreen()
        }
    }
}

/**
 * Helps to set current and parent [Routing] to use current [Navigator]
 * So routing from parent will compose on the current [Navigator]
 */
private fun Routing.linkCurrentNavigatorToParents(navigator: Navigator) {
    var current: Routing? = this
    while (current != null) {
        current.voyagerNavigatorManager.navigator = navigator
        current = current.parent?.asRouting
    }
}
