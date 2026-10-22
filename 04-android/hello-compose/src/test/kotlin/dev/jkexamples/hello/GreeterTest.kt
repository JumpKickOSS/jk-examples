package dev.jkexamples.hello

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GreeterTest {
    @Test
    fun greetsByTrimmedName() {
        assertEquals("Hello, Ada!", Greeter.greeting("  Ada "))
        assertEquals("Hello, whoever you are!", Greeter.greeting(" "))
    }

    @Test
    fun talliesGreetings() {
        assertEquals("No one greeted yet", Greeter.tally(0))
        assertEquals("1 greeting", Greeter.tally(1))
        assertEquals("3 greetings", Greeter.tally(3))
    }
}
