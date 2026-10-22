package dev.jkexamples.tally;

import java.math.BigDecimal;
import java.util.Map;

/** Plain-text rendering of a {@link Ledger}'s totals; Java reading a Groovy class. */
public final class Report {

    private Report() {}

    public static String render(Ledger ledger) {
        Map<String, BigDecimal> totals = ledger.totalsByCategory();
        int width = totals.keySet().stream().mapToInt(String::length).max().orElse(8);
        StringBuilder out = new StringBuilder();
        totals.forEach((category, total) ->
                out.append(String.format("%-" + width + "s  %10.2f%n", category, total)));
        out.append("-".repeat(width + 12)).append('\n');
        out.append(String.format("%-" + width + "s  %10.2f%n", "total", ledger.total()));
        return out.toString();
    }
}
