package cafe.adriel.voyager.routing

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import dev.programadorthi.routing.core.application.Application
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope

public class VoyagerNavigatorManager(
    private val application: Application,
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = application.coroutineContext

    internal var navigator: Navigator? = null

    public fun pop() {
        navigator?.pop()
    }

    public fun popUntil(predicate: (Screen) -> Boolean) {
        navigator?.popUntil(predicate)
    }

    public fun push(screen: Screen) {
        navigator?.push(screen)
    }

    public fun replace(screen: Screen, replaceAll: Boolean) {
        val nav = navigator ?: return
        when {
            replaceAll -> nav.replaceAll(screen)
            else -> nav.replace(screen)
        }
    }
}
