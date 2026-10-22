package dev.jkexamples.hotel.core;

/** Who is staying in one room. */
public record Party(int adults, int children) {

    public Party {
        if (adults < 1) throw new InvalidStayException("a room needs at least one adult");
        if (children < 0) throw new InvalidStayException("children cannot be negative");
    }

    public int size() {
        return adults + children;
    }
}
