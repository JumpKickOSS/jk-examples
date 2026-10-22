package dev.jkexamples.talks;

import java.time.LocalDateTime;

/** A room booked from {@code start} for {@code minutes}; two slots clash when they share a room and overlap. */
public record Slot(String room, LocalDateTime start, int minutes) {

    public LocalDateTime end() {
        return start.plusMinutes(minutes);
    }

    public boolean clashesWith(Slot other) {
        return room.equalsIgnoreCase(other.room) && start.isBefore(other.end()) && other.start.isBefore(end());
    }
}
