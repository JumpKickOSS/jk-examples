package dev.jkexamples.hotel.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PricingPolicyTest {

    private final PricingPolicy policy = new PricingPolicy();

    @Test
    void weekday_nights_cost_the_base_rate_and_tax_is_added() {
        // 2026-11-02 is a Monday: Mon, Tue.
        Quote q = policy.quote(
                Fixtures.HARBOR, Fixtures.KING, new StayDates(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 4)));
        assertThat(q.nights()).extracting(Quote.Night::price).containsOnly(new Money(20_000, "EUR"));
        assertThat(q.subtotal()).isEqualTo(new Money(40_000, "EUR"));
        assertThat(q.taxes()).isEqualTo(new Money(4_800, "EUR"));
        assertThat(q.total()).isEqualTo(new Money(44_800, "EUR"));
    }

    @Test
    void friday_and_saturday_carry_the_weekend_uplift() {
        Quote q = policy.quote(
                Fixtures.HARBOR, Fixtures.KING, new StayDates(LocalDate.of(2026, 11, 6), LocalDate.of(2026, 11, 9)));
        assertThat(q.nights()).extracting(Quote.Night::note).containsExactly("weekend", "weekend", "");
        assertThat(q.subtotal()).isEqualTo(new Money(24_000 + 24_000 + 20_000, "EUR"));
    }

    @Test
    void a_week_or_longer_takes_ten_percent_off_before_tax() {
        Quote q = policy.quote(
                Fixtures.HARBOR, Fixtures.KING, new StayDates(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 9)));
        assertThat(q.subtotal()).isEqualTo(new Money(5 * 20_000 + 2 * 24_000, "EUR"));
        assertThat(q.discount()).isEqualTo(new Money(14_800, "EUR"));
        assertThat(q.total()).isEqualTo(new Money((148_000 - 14_800) * 112 / 100, "EUR"));
    }
}
