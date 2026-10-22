package dev.jkexamples.hotel.service;

import dev.jkexamples.hotel.contract.DeskDay;
import dev.jkexamples.hotel.contract.DeskReservation;
import dev.jkexamples.hotel.contract.OccupancyPoint;
import java.time.LocalDate;
import java.util.List;

/** The staff's side: today's arrivals and departures, check-in and check-out, occupancy. */
public interface FrontDesk {

    DeskDay day(String username, String hotelSlug, LocalDate date);

    DeskReservation checkIn(String username, String confirmationCode);

    DeskReservation checkOut(String username, String confirmationCode);

    List<OccupancyPoint> occupancy(String username, String hotelSlug, LocalDate from, LocalDate to);
}
