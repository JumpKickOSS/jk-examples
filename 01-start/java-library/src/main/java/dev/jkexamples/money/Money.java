package dev.jkexamples.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * An amount in one currency, always at that currency's scale ({@code 2} for EUR, {@code 0} for JPY).
 * Arithmetic between currencies is refused rather than converted.
 */
public record Money(BigDecimal amount, Currency currency) implements Comparable<Money> {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException(amount + " has more decimals than " + currency + " allows ("
                    + currency.getDefaultFractionDigits() + ")");
        }
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
    }

    /** {@code Money.of("12.50", "EUR")}. */
    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money plus(Money other) {
        return new Money(amount.add(same(other).amount), currency);
    }

    public Money minus(Money other) {
        return new Money(amount.subtract(same(other).amount), currency);
    }

    /** This amount times {@code factor}, rounded half-even to the currency's scale. */
    public Money times(BigDecimal factor) {
        return new Money(
                amount.multiply(factor).setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN),
                currency);
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    /**
     * Split this amount by {@code ratios} without creating or losing a minor unit: each share is
     * rounded down and the remainder goes, one unit at a time, to the earliest shares.
     * {@code Money.of("100.00", "EUR").allocate(1, 1, 1)} is {@code 33.34, 33.33, 33.33}.
     */
    public List<Money> allocate(int... ratios) {
        if (ratios.length == 0) throw new IllegalArgumentException("no ratios");
        if (Arrays.stream(ratios).anyMatch(r -> r < 0)) throw new IllegalArgumentException("negative ratio");
        long total = Arrays.stream(ratios).asLongStream().sum();
        if (total == 0) throw new IllegalArgumentException("ratios sum to zero");

        BigDecimal unit = BigDecimal.ONE.movePointLeft(currency.getDefaultFractionDigits());
        long minor = amount.movePointRight(currency.getDefaultFractionDigits()).longValueExact();
        long[] shares = new long[ratios.length];
        long given = 0;
        for (int i = 0; i < ratios.length; i++) {
            shares[i] = Math.floorDiv(minor * ratios[i], total);
            given += shares[i];
        }
        for (int i = 0; given < minor; i = (i + 1) % shares.length) {
            if (ratios[i] == 0) continue;
            shares[i]++;
            given++;
        }
        List<Money> out = new ArrayList<>(shares.length);
        for (long share : shares) out.add(new Money(unit.multiply(BigDecimal.valueOf(share)), currency));
        return List.copyOf(out);
    }

    /** The amount as {@code locale} writes money, e.g. {@code 1.234,50 €} for {@code de-DE}. */
    public String format(Locale locale) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(currency);
        return format.format(amount);
    }

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(same(other).amount);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency.getCurrencyCode();
    }

    private Money same(Money other) {
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException(currency, other.currency);
        }
        return other;
    }
}
