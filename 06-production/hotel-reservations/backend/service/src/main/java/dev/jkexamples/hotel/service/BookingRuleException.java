package dev.jkexamples.hotel.service;

/** A request the rules refuse: a stay that cannot be sold, or a status change that is not allowed. */
public final class BookingRuleException extends RuntimeException {

    public BookingRuleException(String message) {
        super(message);
    }

    /** The same refusal, raised by a domain rule. */
    public BookingRuleException(RuntimeException cause) {
        super(cause.getMessage(), cause);
    }
}
