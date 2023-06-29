package cafe.adriel.voyager.routing

import dev.programadorthi.routing.core.Routing
import dev.programadorthi.routing.core.application
import dev.programadorthi.routing.core.application.Application
import dev.programadorthi.routing.core.application.ApplicationCall
import io.ktor.util.AttributeKey

private val VoyagerNavigatorManagerAttributeKey: AttributeKey<VoyagerNavigatorManager> =
    AttributeKey("VoyagerNavigatorManager")

internal var Application.voyagerNavigatorManager: VoyagerNavigatorManager
    get() = attributes[VoyagerNavigatorManagerAttributeKey]
    set(value) {
        attributes.put(VoyagerNavigatorManagerAttributeKey, value)
    }

public val ApplicationCall.voyagerNavigatorManager: VoyagerNavigatorManager
    get() = application.voyagerNavigatorManager

public val Routing.voyagerNavigatorManager: VoyagerNavigatorManager
    get() = application.voyagerNavigatorManager
