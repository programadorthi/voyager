/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.runtime.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.ControlledComposition
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(InternalComposeApi::class, ExperimentalCoroutinesApi::class)
public fun compositionTest(block: suspend CompositionTestScope.() -> Unit): TestResult = runTest {
    withContext(testMonotonicFrameClock(this)) {
        // Start the recomposer
        val recomposer = Recomposer(coroutineContext)
        launch { recomposer.runRecomposeAndApplyChanges() }
        testScheduler.runCurrent()

        // Create a test scope for the test using the test scope passed in by runTest
        val scope = object : CompositionTestScope, CoroutineScope by this@runTest {
            var composed = false
            var composition: Composition? = null

            override lateinit var root: View

            override val testCoroutineScheduler: TestCoroutineScheduler
                get() = this@runTest.testScheduler

            override fun compose(block: @Composable () -> Unit) {
                check(!composed) { "Compose should only be called once" }
                composed = true
                root = View().apply { name = "root" }
                val composition = Composition(ViewApplier(root), recomposer)
                this.composition = composition
                composition.setContent(block)
            }

            override fun advanceCount(ignorePendingWork: Boolean): Long {
                val changeCount = recomposer.changeCount
                Snapshot.sendApplyNotifications()
                if (recomposer.hasPendingWork) {
                    testScheduler.advanceTimeBy(5_000)
                    check(ignorePendingWork || !recomposer.hasPendingWork) {
                        "Potentially infinite recomposition, still recomposing after advancing"
                    }
                }
                return recomposer.changeCount - changeCount
            }

            override fun advance(ignorePendingWork: Boolean) = advanceCount(ignorePendingWork) != 0L

            override fun verifyConsistent() {
                (composition as? ControlledComposition)?.verifyConsistent()
            }

            override var validator: (MockViewValidator.() -> Unit)? = null
        }
        scope.block()
        scope.composition?.dispose()
        recomposer.cancel()
        recomposer.join()
    }
}

/**
 * A test scope used in tests that allows controlling and testing composition.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public interface CompositionTestScope : CoroutineScope {

    /**
     * A scheduler used by [CoroutineScope]
     */
    public val testCoroutineScheduler: TestCoroutineScheduler

    /**
     * Compose a block using the mock view composer.
     */
    public fun compose(block: @Composable () -> Unit)

    /**
     * Advance the state which executes any pending compositions, if any. Returns true if
     * advancing resulted in changes being applied.
     */
    public fun advance(ignorePendingWork: Boolean = false): Boolean

    /**
     * Advance counting the number of time the recomposer ran.
     */
    public fun advanceCount(ignorePendingWork: Boolean = false): Long

    /**
     * Verify the composition is well-formed.
     */
    public fun verifyConsistent()

    /**
     * The root mock view of the mock views being composed.
     */
    public val root: View

    /**
     * The last validator used.
     */
    public var validator: (MockViewValidator.() -> Unit)?
}

/**
 * Create a mock view validator and validate the view.
 */
public fun CompositionTestScope.validate(block: MockViewValidator.() -> Unit): Unit =
    MockViewListValidator(root.children).validate(block).also { validator = block }

/**
 * Revalidate using the last validator
 */
public fun CompositionTestScope.revalidate(): Unit =
    validate(validator ?: error("validate was not called"))

/**
 * Advance and expect changes
 */
public fun CompositionTestScope.expectChanges() {
    val changes = advance()
    assertTrue(
        actual = changes,
        message = "Expected changes but none were found"
    )
}

/**
 * Advance and expect no changes
 */
public fun CompositionTestScope.expectNoChanges() {
    val changes = advance()
    assertFalse(
        actual = changes,
        message = "Expected no changes but changes occurred"
    )
}
