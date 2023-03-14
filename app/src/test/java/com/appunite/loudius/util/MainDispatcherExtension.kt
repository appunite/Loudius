package com.appunite.loudius.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherExtension(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : BeforeTestExecutionCallback, AfterTestExecutionCallback {
    override fun beforeTestExecution(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterTestExecution(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
