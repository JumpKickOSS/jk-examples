package dev.jkexamples.tally

import spock.lang.Specification

import java.time.LocalDate

class LedgerSpec extends Specification {

    static final String CSV = '''\
date,category,amount,note
2026-10-01,groceries,42.10,market
2026-10-03,transport,2.75,bus
2026-10-04,groceries,18.40,
2026-10-09,rent,1200.00,october
'''

    def "totals are summed per category, largest first"() {
        when:
        def ledger = Ledger.parse(new StringReader(CSV))

        then:
        ledger.totalsByCategory() == [rent: 1200.00G, groceries: 60.50G, transport: 2.75G]
        ledger.total() == 1263.25G
    }

    def "a date window keeps only the days inside it"() {
        given:
        def ledger = Ledger.parse(new StringReader(CSV))

        expect:
        ledger.between(LocalDate.parse(from), LocalDate.parse(to)).total() == total

        where:
        from         | to           || total
        '2026-10-01' | '2026-10-31' || 1263.25G
        '2026-10-02' | '2026-10-04' || 21.15G
        '2026-10-10' | '2026-10-31' || 0G
    }

    def "a short row names its line"() {
        when:
        Ledger.parse(new StringReader('date,category,amount\n2026-10-01,food\n'))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.startsWith('line 2')
    }

    def "Java renders the Groovy ledger"() {
        expect:
        Report.render(Ledger.parse(new StringReader(CSV))).readLines().last() ==~ /total\s+1263\.25/
    }
}
