package com.appunite.loudius

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appunite.loudius.ui.login.LoginScreen
import com.appunite.loudius.ui.theme.LoudiusTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LoginScreenTest {

//    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
//    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TestActivity>()
    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(hiltRule)
        .around(composeTestRule)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun whenTheLoginScreenIsVisibleThenTheLogInButtonIsVisible() {
        composeTestRule.setContent {
            LoudiusTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Log in").assertIsDisplayed()
    }
}
