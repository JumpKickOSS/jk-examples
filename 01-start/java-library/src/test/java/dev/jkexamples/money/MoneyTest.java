package dev.jkexamples.money;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTest {

    @Test
    void amounts_take_the_currency_scale() {
        assertEquals("12.50 EUR", Money.of("12.5", "EUR").toString());
        assertEquals("1200 JPY", Money.of("1200", "JPY").toString());
        assertThrows(IllegalArgumentException.class, () -> Money.of("1.005", "EUR"));
    }

    @Test
    void currencies_never_mix() {
        assertThrows(CurrencyMismatchException.class, () -> Money.of("1", "EUR").plus(Money.of("1", "USD")));
    }

    @Test
    void times_rounds_half_even() {
        assertEquals(Money.of("0.12", "EUR"), Money.of("0.25", "EUR").times(new BigDecimal("0.5")));
        assertEquals(Money.of("0.38", "EUR"), Money.of("0.75", "EUR").times(new BigDecimal("0.5")));
    }

    @Test
    void allocation_hands_the_remainder_to_the_first_shares() {
        assertEquals(
                List.of(Money.of("33.34", "EUR"), Money.of("33.33", "EUR"), Money.of("33.33", "EUR")),
                Money.of("100.00", "EUR").allocate(1, 1, 1));
    }

    @ParameterizedTest
    @CsvSource({
        "100.00,EUR,'1,1,1'",
        "0.05,EUR,'3,7'",
        "1000,JPY,'1,2,3,4,5,6'",
        "9.99,USD,'70,20,10'",
        "0.01,EUR,'0,1'"
    })
    void allocation_never_loses_a_minor_unit(String amount, String currency, String ratios) {
        Money total = Money.of(amount, currency);
        int[] parts =
                Arrays.stream(ratios.split(",")).mapToInt(Integer::parseInt).toArray();
        Money sum = total.allocate(parts).stream().reduce(Money.zero(total.currency()), Money::plus);
        assertEquals(total, sum);
    }

    @Test
    void formats_for_a_locale() {
        assertEquals("€12.50", Money.of("12.50", "EUR").format(Locale.UK));
    }
}
