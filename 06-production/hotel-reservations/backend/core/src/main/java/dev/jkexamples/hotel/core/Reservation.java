package dev.jkexamples.hotel.core;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** A booked room for a stay. */
public record Reservation(
        UUID id,
        String confirmationCode,
        UUID hotelId,
        UUID roomId,
        String roomNumber,
        UUID roomTypeId,
        String roomTypeName,
        Guest guest,
        StayDates stay,
        Party party,
        ReservationStatus status,
        Money total,
        String specialRequests,
        Instant bookedAt) {

    /** A guest may cancel a booking that has not started; the desk handles the rest. */
    public boolean cancellableOn(LocalDate today) {
        return status == ReservationStatus.BOOKED && today.isBefore(stay.checkIn());
    }

    /** True when {@code lastName} matches the guest's, ignoring case and surrounding space. */
    public boolean belongsTo(String lastName) {
        return guest.lastName().strip().equalsIgnoreCase(lastName.strip());
    }
}
