package dev.jkexamples.hotel.contract;

import java.time.LocalDate;

/** The price of one night, with the reason it differs from the base rate when it does. */
public record NightlyPrice(LocalDate night, MoneyView price, String note) {}
