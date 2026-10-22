package dev.jkexamples.hotel.core;

/** A stay the hotels cannot sell: backwards, too long, or too many guests for the room. */
public final class InvalidStayException extends IllegalArgumentException {

    public InvalidStayException(String message) {
        super(message);
    }
}
