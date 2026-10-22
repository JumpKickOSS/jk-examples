package dev.jkexamples.hotel.contract;

import java.time.LocalDate;

/** A reservation as the front desk works it. */
public record DeskReservation(
        String confirmationCode,
        String status,
        String guestName,
        String roomNumber,
        String roomTypeName,
        LocalDate checkIn,
        LocalDate checkOut,
        int guests) {}
