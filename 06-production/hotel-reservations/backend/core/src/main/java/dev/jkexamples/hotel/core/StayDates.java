package dev.jkexamples.hotel.core;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** The nights between check-in and check-out: check-in is the first night, check-out is not a night. */
public record StayDates(LocalDate checkIn, LocalDate checkOut) {

    /** The longest stay the hotels sell online. */
    public static final int MAX_NIGHTS = 30;

    public StayDates {
        if (!checkOut.isAfter(checkIn)) throw new InvalidStayException("check-out must be after check-in");
        if (ChronoUnit.DAYS.between(checkIn, checkOut) > MAX_NIGHTS) {
            throw new InvalidStayException("a stay is at most " + MAX_NIGHTS + " nights");
        }
    }

    public int nights() {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public List<LocalDate> eachNight() {
        return checkIn.datesUntil(checkOut).toList();
    }

    public boolean overlaps(StayDates other) {
        return checkIn.isBefore(other.checkOut) && other.checkIn.isBefore(checkOut);
    }

    public boolean includesNight(LocalDate night) {
        return !night.isBefore(checkIn) && night.isBefore(checkOut);
    }
}
