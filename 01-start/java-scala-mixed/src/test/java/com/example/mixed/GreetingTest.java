// SPDX-License-Identifier: Apache-2.0
package com.example.mixed;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** A Java test of the Java half, which runs through the Scala class. */
class GreetingTest {

    @Test
    void greets_through_the_scala_greeter() {
        assertEquals("Hello, Ada!", Greeting.of("Ada"));
    }

    @Test
    void the_default_name_is_world() {
        assertEquals("world", Greeting.defaultName());
    }
}
