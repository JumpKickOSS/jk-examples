package dev.jkexamples.hotel.contract;

/** An amount in minor units (cents) and its ISO 4217 currency. */
public record MoneyView(long minor, String currency) {}
