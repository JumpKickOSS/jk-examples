package dev.jkexamples.hotel.contract;

import java.util.List;

/** A hotel as the search page lists it. {@code fromPrice} is the cheapest nightly base rate. */
public record HotelSummary(
        String slug,
        String name,
        String city,
        String country,
        int stars,
        String tagline,
        String heroImage,
        MoneyView fromPrice,
        List<String> amenities) {}
