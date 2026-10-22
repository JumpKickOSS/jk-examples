package dev.jkexamples.talks;

import io.quarkus.runtime.Quarkus;

/** A plain {@code main}, so the test bootstrap indexes the application once. */
public class Application {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}
