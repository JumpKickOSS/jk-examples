package dev.jkexamples.hotel.core;

import java.util.Set;

/** Where a reservation is in its life. Every move not listed in {@link #next()} is refused. */
public enum ReservationStatus {
    BOOKED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED;

    public Set<ReservationStatus> next() {
        return switch (this) {
            case BOOKED -> Set.of(CHECKED_IN, CANCELLED);
            case CHECKED_IN -> Set.of(CHECKED_OUT);
            case CHECKED_OUT, CANCELLED -> Set.of();
        };
    }

    public ReservationStatus moveTo(ReservationStatus target) {
        if (!next().contains(target)) {
            throw new IllegalStateException("a " + this + " reservation cannot become " + target);
        }
        return target;
    }
}
