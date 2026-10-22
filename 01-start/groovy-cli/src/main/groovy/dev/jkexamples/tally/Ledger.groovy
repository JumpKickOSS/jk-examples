package dev.jkexamples.tally

import groovy.transform.CompileStatic

import java.time.LocalDate

/** Expenses parsed from CSV ({@code date,category,amount,note}) and the sums over them. */
@CompileStatic
class Ledger {

    final List<Expense> expenses

    Ledger(List<Expense> expenses) {
        this.expenses = List.copyOf(expenses)
    }

    static Ledger parse(Reader csv) {
        List<Expense> rows = []
        csv.eachLine { String line, int number ->
            if (number == 1 || !line.trim()) return
            List<String> cells = line.split(',', 4)*.trim()
            if (cells.size() < 3) throw new IllegalArgumentException("line $number: expected date,category,amount[,note]")
            rows << new Expense(LocalDate.parse(cells[0]), cells[1], new BigDecimal(cells[2]), cells[3] ?: '')
        }
        new Ledger(rows)
    }

    Ledger between(LocalDate from, LocalDate to) {
        new Ledger(expenses.findAll { Expense e -> !e.date().isBefore(from) && !e.date().isAfter(to) })
    }

    /** Totals per category, largest first. */
    Map<String, BigDecimal> totalsByCategory() {
        Map<String, BigDecimal> sums = [:].withDefault { BigDecimal.ZERO } as Map<String, BigDecimal>
        expenses.each { Expense e -> sums[e.category()] = sums[e.category()] + e.amount() }
        sums.sort { a, b -> b.value <=> a.value } as LinkedHashMap<String, BigDecimal>
    }

    BigDecimal total() {
        expenses.sum(BigDecimal.ZERO) { Expense e -> e.amount() } as BigDecimal
    }
}
