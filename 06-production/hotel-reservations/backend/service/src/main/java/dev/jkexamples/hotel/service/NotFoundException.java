package dev.jkexamples.hotel.service;

/** No such hotel, room type or reservation — or one the caller may not see. */
public final class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
