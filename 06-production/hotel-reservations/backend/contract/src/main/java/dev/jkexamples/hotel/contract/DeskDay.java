package dev.jkexamples.hotel.contract;

import java.time.LocalDate;
import java.util.List;

/** One hotel's day at the front desk. {@code occupancyPercent} counts rooms occupied tonight. */
public record DeskDay(
        String hotelSlug,
        String hotelName,
        LocalDate date,
        int rooms,
        int occupancyPercent,
        List<DeskReservation> arrivals,
        List<DeskReservation> departures,
        List<DeskReservation> inHouse) {}
