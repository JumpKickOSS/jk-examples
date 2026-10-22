package dev.jkexamples.hotel.service;

import dev.jkexamples.hotel.contract.Availability;
import dev.jkexamples.hotel.contract.HotelDetail;
import dev.jkexamples.hotel.contract.HotelSummary;
import dev.jkexamples.hotel.contract.SearchResult;
import java.time.LocalDate;
import java.util.List;

/** The hotels, their rooms and what is free when. */
public interface HotelCatalog {

    List<HotelSummary> hotels();

    HotelDetail hotel(String slug);

    /** Every hotel with a room for {@code guests} for the whole stay, cheapest total first. */
    List<SearchResult> search(LocalDate checkIn, LocalDate checkOut, int guests);

    Availability availability(String slug, LocalDate checkIn, LocalDate checkOut, int guests);
}
