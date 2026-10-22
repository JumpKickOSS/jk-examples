package dev.jkexamples.hello

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The screen itself, rendered by Robolectric on the JVM: type, tap, read. */
@RunWith(RobolectricTestRunner::class)
class GreetingScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun typingANameAndTappingGreets() {
        compose.setContent { GreetingScreen(GreetingViewModel()) }
        compose.onNodeWithText("No one greeted yet").assertExists()
        compose.onNodeWithText("Your name").performTextInput("Ada")
        compose.onNodeWithText("Say hello").performClick()
        compose.onNodeWithText("Hello, Ada!").assertExists()
        compose.onNodeWithText("1 greeting").assertExists()
    }
}
