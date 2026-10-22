package dev.jkexamples.hotel.core;

/** Rooms sold against rooms the hotel has, for one night. */
public record Occupancy(int occupied, int rooms) {

    public Occupancy {
        if (rooms < 0 || occupied < 0 || occupied > rooms) throw new IllegalArgumentException(occupied + "/" + rooms);
    }

    /** Whole percent, rounded half-up; 0 for a hotel with no rooms. */
    public int percent() {
        return rooms == 0 ? 0 : (occupied * 200 + rooms) / (2 * rooms);
    }
}
