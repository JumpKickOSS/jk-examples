package dev.jkexamples.money;

import java.util.Currency;

/** Arithmetic between two currencies. Convert first; {@link Money} never converts. */
public final class CurrencyMismatchException extends IllegalArgumentException {

    public CurrencyMismatchException(Currency left, Currency right) {
        super("cannot combine " + left.getCurrencyCode() + " with " + right.getCurrencyCode());
    }
}
