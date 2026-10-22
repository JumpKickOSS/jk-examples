package dev.jkexamples.greeter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** Prints the greeting the hello plugin wrote into this module's resources at build time. */
public final class Greeter {

    private Greeter() {}

    public static void main(String[] args) throws IOException {
        try (InputStream in = Objects.requireNonNull(
                Greeter.class.getResourceAsStream("/greeting.txt"), "greeting.txt: is the hello plugin trusted?")) {
            System.out.print(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
