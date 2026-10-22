package dev.jkexamples.hotel.core;

import java.time.LocalDate;
import java.util.List;

/** What a stay in one room type costs, night by night, with discount and tax shown apart. */
public record Quote(List<Night> nights, Money subtotal, Money discount, Money taxes, Money total) {

    /** One night's price; {@code note} says why it is not the base rate ("weekend"), or is empty. */
    public record Night(LocalDate date, Money price, String note) {}
}
