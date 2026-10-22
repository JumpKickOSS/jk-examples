package dev.jkexamples.hotel.service;

import dev.jkexamples.hotel.contract.BookingRequest;
import dev.jkexamples.hotel.contract.LookupRequest;
import dev.jkexamples.hotel.contract.ReservationView;

/** A guest's side of a reservation: book it, find it, cancel it. */
public interface Bookings {

    /** Books one room of the requested type, or throws {@link RoomUnavailableException}. */
    ReservationView book(BookingRequest request);

    /** The reservation with this code, when the last name matches; {@link NotFoundException} otherwise. */
    ReservationView find(LookupRequest lookup);

    ReservationView cancel(LookupRequest lookup);
}
