package dev.jkexamples.hotel.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** An amount in minor units (cents) of one currency. Arithmetic across currencies is refused. */
public record Money(long minor, String currency) {

    public Money {
        if (currency.length() != 3) throw new IllegalArgumentException("not an ISO 4217 code: " + currency);
    }

    public static Money zero(String currency) {
        return new Money(0, currency);
    }

    public Money plus(Money other) {
        return new Money(Math.addExact(minor, same(other).minor), currency);
    }

    public Money minus(Money other) {
        return new Money(Math.subtractExact(minor, same(other).minor), currency);
    }

    /** This amount scaled by {@code percent} (e.g. {@code 120} for +20 %), rounded half-up to a cent. */
    public Money percent(int percent) {
        return new Money(
                BigDecimal.valueOf(minor)
                        .multiply(BigDecimal.valueOf(percent))
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                        .longValueExact(),
                currency);
    }

    /** This amount times a rate in basis points ({@code 1200} is 12 %), rounded half-up to a cent. */
    public Money basisPoints(int bp) {
        return new Money(
                BigDecimal.valueOf(minor)
                        .multiply(BigDecimal.valueOf(bp))
                        .divide(BigDecimal.valueOf(10_000), 0, RoundingMode.HALF_UP)
                        .longValueExact(),
                currency);
    }

    private Money same(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalArgumentException(currency + " vs " + other.currency);
        return other;
    }
}
