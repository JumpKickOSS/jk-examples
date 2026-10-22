package dev.jkexamples.hello

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** A ViewModel is a plain class: no device, no Robolectric. */
class GreetingViewModelTest {
    @Test
    fun greetingUsesTheTypedNameAndCounts() {
        val model = GreetingViewModel()
        model.onNameChange("Grace")
        model.greet()
        model.greet()
        assertEquals(GreetingState(name = "Grace", greeting = "Hello, Grace!", count = 2), model.ui.value)
    }
}
