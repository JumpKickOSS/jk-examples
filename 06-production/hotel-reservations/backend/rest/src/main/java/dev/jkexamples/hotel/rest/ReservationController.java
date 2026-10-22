package dev.jkexamples.hotel.rest;

import dev.jkexamples.hotel.contract.BookingRequest;
import dev.jkexamples.hotel.contract.LookupRequest;
import dev.jkexamples.hotel.contract.ReservationView;
import dev.jkexamples.hotel.service.Bookings;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A guest's reservations. Lookups are POSTs: the last name is a credential and stays out of URLs,
 * access logs and browser history.
 */
@RestController
@RequestMapping("/api/reservations")
class ReservationController {

    private final Bookings bookings;

    ReservationController(Bookings bookings) {
        this.bookings = bookings;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ReservationView book(@Valid @RequestBody BookingRequest request) {
        return bookings.book(request);
    }

    @PostMapping("/lookup")
    ReservationView find(@Valid @RequestBody LookupRequest lookup) {
        return bookings.find(lookup);
    }

    @PostMapping("/cancel")
    ReservationView cancel(@Valid @RequestBody LookupRequest lookup) {
        return bookings.cancel(lookup);
    }
}
