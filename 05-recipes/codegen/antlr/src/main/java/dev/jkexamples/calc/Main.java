package dev.jkexamples.calc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** {@code calc FILE} runs a program; with no file, standard input. */
public final class Main {

    private Main() {}

    public static void main(String[] args) throws IOException {
        String program = args.length == 0
                ? new String(System.in.readAllBytes(), StandardCharsets.UTF_8)
                : Files.readString(Path.of(args[0]));
        try {
            Calc.run(program).forEach(System.out::println);
        } catch (CalcException e) {
            System.err.println("calc: " + e.getMessage());
            System.exit(1);
        }
    }
}
