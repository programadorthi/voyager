package cafe.adriel.voyager.routing.typesafe

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

internal class TestScreen(val value: Any) : Screen {

    @Composable
    override fun Content() {}
}