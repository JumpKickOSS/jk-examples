package dev.jkexamples.hotel.service;

/** Every room of the requested type is taken for some night of the stay. */
public final class RoomUnavailableException extends RuntimeException {

    public RoomUnavailableException(String message) {
        super(message);
    }
}
