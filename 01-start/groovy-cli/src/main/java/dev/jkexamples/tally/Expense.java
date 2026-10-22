package dev.jkexamples.tally;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One line of the ledger. Amounts are in the ledger's single currency. */
public record Expense(LocalDate date, String category, BigDecimal amount, String note) {}
