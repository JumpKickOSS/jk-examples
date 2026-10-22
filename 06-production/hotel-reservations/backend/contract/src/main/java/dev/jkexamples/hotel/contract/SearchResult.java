package dev.jkexamples.hotel.contract;

/** A hotel with rooms for the requested stay and the lowest total it can offer. */
public record SearchResult(HotelSummary hotel, MoneyView lowestTotal, int nights) {}
