package dev.jkexamples.hotel.contract;

import java.time.LocalDate;
import java.util.List;

/** Every offer one hotel can make for a stay; empty when nothing fits. */
public record Availability(String hotelSlug, LocalDate checkIn, LocalDate checkOut, int guests, List<Offer> offers) {}
