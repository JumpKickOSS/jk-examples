package dev.jkexamples.hotel.contract;

import java.time.LocalDate;

/** Rooms occupied on one night. */
public record OccupancyPoint(LocalDate night, int occupied, int rooms) {}
