package dev.jkexamples.sortlines;

import java.util.Arrays;
import java.util.stream.Collectors;

/** The text work, with no IDE types: sorts lines case-insensitively and keeps a trailing newline. */
public final class LineSorter {

    private LineSorter() {}

    public static String sort(String text) {
        boolean trailingNewline = text.endsWith("\n");
        String body = trailingNewline ? text.substring(0, text.length() - 1) : text;
        String sorted = Arrays.stream(body.split("\n", -1))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining("\n"));
        return trailingNewline ? sorted + "\n" : sorted;
    }
}
