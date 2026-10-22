// SPDX-License-Identifier: Apache-2.0
package com.example.mixed;

/** The Java half: calls into the Scala {@link Greeter} and lends {@code Main} its default name. */
public final class Greeting {

    /** What every greeting opens with. */
    public static final String SALUTATION = "Hello";

    private Greeting() {}

    /** The greeting for {@code name}, built by the Scala class. */
    public static String of(String name) {
        return new Greeter(SALUTATION).greet(name);
    }

    /** Whom {@code Main} greets when no argument is given. */
    public static String defaultName() {
        return "world";
    }
}
