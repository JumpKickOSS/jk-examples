package dev.jkexamples.tally

import groovy.cli.picocli.CliBuilder
import groovy.json.JsonOutput

import java.time.LocalDate

/** {@code tally [--from DATE] [--to DATE] [--json] FILE}: expense totals by category. */
class Main {

    static void main(String[] args) {
        def cli = new CliBuilder(name: 'tally', usage: 'tally [options] FILE')
        cli.f(longOpt: 'from', args: 1, argName: 'date', 'first day to count (yyyy-mm-dd)')
        cli.t(longOpt: 'to', args: 1, argName: 'date', 'last day to count (yyyy-mm-dd)')
        cli.j(longOpt: 'json', 'print JSON instead of a table')
        cli.h(longOpt: 'help', 'show this help')
        def options = cli.parse(args)
        if (!options) System.exit(2)
        if (options.h || options.arguments().size() != 1) {
            cli.usage()
            System.exit(options.h ? 0 : 2)
        }

        def ledger = new File(options.arguments()[0]).withReader { Ledger.parse(it) }
        if (options.f || options.t) {
            ledger = ledger.between(
                    options.f ? LocalDate.parse(options.f as String) : LocalDate.MIN,
                    options.t ? LocalDate.parse(options.t as String) : LocalDate.MAX)
        }
        if (options.j) {
            println JsonOutput.prettyPrint(JsonOutput.toJson(
                    categories: ledger.totalsByCategory(), total: ledger.total()))
        } else {
            print Report.render(ledger)
        }
    }
}
