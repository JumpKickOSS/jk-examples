package dev.jkexamples.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LedgerTest {

    @Test
    void sums_each_account() throws Exception {
        var balances = Ledger.balances(new StringReader("""
                account,amount
                rent,-1200.00
                salary,4100.50
                rent,-1200.00
                """));
        assertThat(balances)
                .containsEntry("rent", new BigDecimal("-2400.00"))
                .containsEntry("salary", new BigDecimal("4100.50"));
    }
}
