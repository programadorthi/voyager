package cafe.adriel.voyager.sample.multiplatform.routing

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.routing.VoyagerNavigator
import cafe.adriel.voyager.routing.VoyagerRouter
import cafe.adriel.voyager.routing.screen
import cafe.adriel.voyager.routing.typesafe.screen
import cafe.adriel.voyager.sample.multiplatform.routing.screens.CartScreen
import cafe.adriel.voyager.sample.multiplatform.routing.screens.CatalogScreen
import cafe.adriel.voyager.sample.multiplatform.routing.screens.HomeScreen
import cafe.adriel.voyager.sample.multiplatform.routing.screens.LoginScreen
import cafe.adriel.voyager.sample.multiplatform.routing.screens.LogoutScreen
import cafe.adriel.voyager.sample.multiplatform.routing.typesafe.LoginRoute
import dev.programadorthi.routing.core.install
import dev.programadorthi.routing.core.route
import dev.programadorthi.routing.core.routing
import dev.programadorthi.routing.resources.Resources

val router = routing {
    install(Resources)
    install(VoyagerNavigator)

    screen(path = "/", name = "home") {
        HomeScreen()
    }

    screen<LoginRoute> {
        LoginScreen()
    }

    screen(path = "/logout", name = "logout") {
        LogoutScreen()
    }

    route(path = "/catalog", name = "catalog") {
        // Screen to show when routing /catalog
        screen {
            CatalogScreen()
        }

        // Nested route to /catalog/cart
        screen(path = "cart", name = "cart") {
            CartScreen()
        }
    }
}

@Composable
fun SampleApplication() {
    VoyagerRouter(router = router, initialScreen = HomeScreen())
}
