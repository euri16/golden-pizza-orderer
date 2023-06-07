package dev.eury.goldenpizza.testing.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainCoroutineRule(isUnconfined: Boolean = true) : TestWatcher() {

    @SuppressWarnings("unused")
    val testCoroutineDispatcher =
        if (isUnconfined) UnconfinedTestDispatcher() else StandardTestDispatcher()

    @Suppress("unused")
    val testScope = TestScope(testCoroutineDispatcher)

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
