package com.example.mixed

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** A Scala test of the Scala half, in the same JUnit tier as the Java one. */
class GreeterTest:
  @Test
  def greetsEveryName(): Unit =
    assertEquals("Hi, Ada!\nHi, Grace!", Greeter("Hi").greetAll(Seq("Ada", "Grace")))

  @Test
  def usesTheJavaSalutation(): Unit =
    assertEquals(Greeting.of("Ada"), Greeter(Greeting.SALUTATION).greet("Ada"))
