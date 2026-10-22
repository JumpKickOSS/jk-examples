package dev.jkexamples.loans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AmortizationTest {

    @Test
    void a_thirty_year_mortgage() {
        var loan = new Amortization(new BigDecimal("300000"), new BigDecimal("6.5"), 30);
        assertEquals(new BigDecimal("1896.20"), loan.monthlyPayment());
        assertEquals(new BigDecimal("382632.00"), loan.totalInterest());
    }

    @Test
    void no_interest_divides_evenly() {
        var loan = new Amortization(new BigDecimal("12000"), BigDecimal.ZERO, 1);
        assertEquals(new BigDecimal("1000.00"), loan.monthlyPayment());
        assertEquals(new BigDecimal("0.00"), loan.totalInterest());
    }

    @Test
    void a_term_out_of_range_is_refused() {
        assertThrows(IllegalArgumentException.class, () -> new Amortization(BigDecimal.TEN, BigDecimal.ONE, 0));
    }
}
