package cafe.adriel.voyager.routing

import cafe.adriel.voyager.core.screen.Screen
import dev.programadorthi.routing.core.Routing
import dev.programadorthi.routing.core.application

public fun Routing.popUntil(predicate: (Screen) -> Boolean) {
    // TODO: We need to clear path stack when using popUntil
    application.voyagerNavigatorManager.popUntil(predicate)
}
