package cafe.adriel.voyager.routing

import dev.programadorthi.routing.core.application.Application
import dev.programadorthi.routing.core.application.ApplicationCall
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes

private val VoyagerNavigatorManagerAttributeKey: AttributeKey<VoyagerNavigatorManager> =
    AttributeKey("VoyagerNavigatorManager")

private var Attributes.voyagerNavigatorManager: VoyagerNavigatorManager
    get() = get(VoyagerNavigatorManagerAttributeKey)
    private set(value) {
        put(VoyagerNavigatorManagerAttributeKey, value)
    }

internal var Application.voyagerNavigatorManager: VoyagerNavigatorManager
    get() = attributes.voyagerNavigatorManager
    set(value) {
        attributes.voyagerNavigatorManager = value
    }

public var ApplicationCall.voyagerNavigatorManager: VoyagerNavigatorManager
    get() = application.voyagerNavigatorManager
    internal set(value) {
        application.voyagerNavigatorManager = value
    }