package dev.jkexamples.hotel.core;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * The chain's rate rules. Friday and Saturday nights cost {@value #WEEKEND_PERCENT} % of the base
 * rate; a stay of {@value #LONG_STAY_NIGHTS} nights or more takes {@value #LONG_STAY_DISCOUNT_PERCENT} %
 * off the room subtotal; lodging tax applies to what remains.
 */
public final class PricingPolicy {

    public static final int WEEKEND_PERCENT = 120;
    public static final int LONG_STAY_NIGHTS = 7;
    public static final int LONG_STAY_DISCOUNT_PERCENT = 10;

    public Quote quote(Hotel hotel, RoomType roomType, StayDates stay) {
        Money base = roomType.baseRate();
        List<Quote.Night> nights = new ArrayList<>(stay.nights());
        Money subtotal = Money.zero(base.currency());
        for (var date : stay.eachNight()) {
            boolean weekend = date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY;
            Money price = weekend ? base.percent(WEEKEND_PERCENT) : base;
            nights.add(new Quote.Night(date, price, weekend ? "weekend" : ""));
            subtotal = subtotal.plus(price);
        }
        Money discount = stay.nights() >= LONG_STAY_NIGHTS
                ? subtotal.percent(LONG_STAY_DISCOUNT_PERCENT)
                : Money.zero(base.currency());
        Money taxes = subtotal.minus(discount).basisPoints(hotel.taxBasisPoints());
        return new Quote(
                List.copyOf(nights),
                subtotal,
                discount,
                taxes,
                subtotal.minus(discount).plus(taxes));
    }
}
