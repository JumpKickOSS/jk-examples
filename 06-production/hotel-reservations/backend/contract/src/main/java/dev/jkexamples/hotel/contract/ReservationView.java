package dev.jkexamples.hotel.contract;

import java.time.Instant;
import java.time.LocalDate;

/** A reservation as its guest sees it. */
public record ReservationView(
        String confirmationCode,
        String status,
        String hotelSlug,
        String hotelName,
        String roomTypeName,
        String roomNumber,
        LocalDate checkIn,
        LocalDate checkOut,
        int nights,
        int adults,
        int children,
        String guestName,
        String guestEmail,
        MoneyView total,
        String specialRequests,
        boolean cancellable,
        Instant bookedAt) {}
