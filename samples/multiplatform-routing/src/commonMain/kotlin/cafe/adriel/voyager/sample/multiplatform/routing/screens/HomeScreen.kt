package cafe.adriel.voyager.sample.multiplatform.routing.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.routing.VoyagerLocalRouter
import cafe.adriel.voyager.sample.multiplatform.routing.model.ViewModel
import cafe.adriel.voyager.sample.multiplatform.routing.typesafe.LoginRoute
import dev.programadorthi.routing.core.push
import dev.programadorthi.routing.core.pushNamed
import dev.programadorthi.routing.resources.push

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val router = VoyagerLocalRouter.currentOrThrow
        val signedIn by remember { ViewModel.signedIn }

        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Welcome to Home Screen")
            Spacer(modifier = Modifier.height(15.dp))

            Text(text = "Click below to push ${if (signedIn) "Logout" else "Login"} Screen by path")
            Button(onClick = {
                if (signedIn) {
                    router.push(path = "/logout")
                } else {
                    router.push(LoginRoute())
                }
            }) {
                Text(text = "PUSH ${if (signedIn) "LOGOUT" else "LOGIN"} SCREEN")
            }
            Spacer(modifier = Modifier.height(15.dp))

            Text(text = "Click below to push Catalog Screen by name")
            Button(onClick = {
                router.pushNamed(name = "catalog")
            }) {
                Text(text = "PUSH CATALOG SCREEN")
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
