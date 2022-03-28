package cafe.adriel.voyager.core.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.testing.compositionTest
import androidx.compose.runtime.testing.expectChanges
import androidx.compose.runtime.testing.expectNoChanges
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ScreenLifecycleTest {
    @Test
    fun `should call onStart when lifecycle effect starts`() = compositionTest {
        val screen = TestScreen()
        val onStartTrigger = TestTrigger()

        compose {
            onStartTrigger.subscribe()
            screen.LifecycleEffect(
                onStarted = onStartTrigger::increase
            )
        }

        val expectedTimes = 1
        val resultTimes = onStartTrigger.count

        assertEquals(expectedTimes, resultTimes)
        expectNoChanges()
        assertEquals(expectedTimes, resultTimes)
    }

    @Test
    fun `should not call onStart again when lifecycle effect recompose same screen`() = compositionTest {
        val screen = TestScreen()
        val onStartTrigger = TestTrigger()

        compose {
            onStartTrigger.subscribe()
            screen.LifecycleEffect(
                onStarted = onStartTrigger::increase
            )
        }

        assertEquals(1, onStartTrigger.count)
        onStartTrigger.recompose()
        expectNoChanges()
        assertEquals(1, onStartTrigger.count)
    }

    @Test
    fun `should call onStart again when lifecycle effect recompose with other screen`() = compositionTest {
        var screen = TestScreen()
        val onStartTrigger = TestTrigger()

        compose {
            onStartTrigger.subscribe()
            screen.LifecycleEffect(
                onStarted = onStartTrigger::increase
            )
            screen = TestScreen(key = "Key2")
        }

        assertEquals(1, onStartTrigger.count)
        onStartTrigger.recompose()
        expectChanges()
        assertEquals(2, onStartTrigger.count)
    }

    @Test
    fun `should not call onDispose when lifecycle effect starts`() = compositionTest {
        val screen = TestScreen()
        val onDisposeTrigger = TestTrigger()

        compose {
            onDisposeTrigger.subscribe()
            screen.LifecycleEffect(
                onDisposed = onDisposeTrigger::increase
            )
        }

        val expectedTimes = 0
        val resultTimes = onDisposeTrigger.count

        assertEquals(expectedTimes, resultTimes)
        expectNoChanges()
        assertEquals(expectedTimes, resultTimes)
    }

    @Test
    fun `should not call onDispose when lifecycle effect recompose same screen`() = compositionTest {
        val screen = TestScreen()
        val onDisposeTrigger = TestTrigger()

        compose {
            onDisposeTrigger.subscribe()
            screen.LifecycleEffect(
                onDisposed = onDisposeTrigger::increase
            )
        }

        assertEquals(0, onDisposeTrigger.count)
        onDisposeTrigger.recompose()
        expectNoChanges()
        assertEquals(0, onDisposeTrigger.count)
    }

    @Test
    fun `should call onDispose when lifecycle effect recompose with other screen`() = compositionTest {
        var screen = TestScreen()
        val onDisposeTrigger = TestTrigger()

        compose {
            onDisposeTrigger.subscribe()
            screen.LifecycleEffect(
                onDisposed = onDisposeTrigger::increase
            )
            screen = TestScreen(key = "Key2")
        }

        assertEquals(0, onDisposeTrigger.count)
        onDisposeTrigger.recompose()
        expectChanges()
        assertEquals(1, onDisposeTrigger.count)
    }

    @Test
    fun `should restart when lifecycle effect recompose with other screen`() = compositionTest {
        var screen = TestScreen()
        val onStartTrigger = TestTrigger()
        val onDisposeTrigger = TestTrigger()

        compose {
            onStartTrigger.subscribe()
            onDisposeTrigger.subscribe()
            screen.LifecycleEffect(
                onDisposed = onDisposeTrigger::increase,
                onStarted = onStartTrigger::increase
            )
            screen = TestScreen(key = "Key2")
        }

        assertEquals(0, onDisposeTrigger.count)
        assertEquals(1, onStartTrigger.count)
        onDisposeTrigger.recompose()
        expectChanges()
        assertEquals(1, onDisposeTrigger.count)
        assertEquals(2, onStartTrigger.count)
    }
}

internal class TestScreen(
    override val key: ScreenKey = "ScreenKey"
) : Screen {
    @Composable
    override fun Content() = Unit
}

internal class TestTrigger {
    private val _count = mutableStateOf(0)
    private val recompose = mutableStateOf(false)

    val count: Int get() = _count.value

    fun increase() { _count.value += 1 }

    fun subscribe() {
        _count.value
        recompose.value
    }

    fun recompose() { recompose.value = !recompose.value }
}
