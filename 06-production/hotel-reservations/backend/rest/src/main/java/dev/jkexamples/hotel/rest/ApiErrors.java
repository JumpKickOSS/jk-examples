package dev.jkexamples.hotel.rest;

import dev.jkexamples.hotel.service.BookingRuleException;
import dev.jkexamples.hotel.service.NotFoundException;
import dev.jkexamples.hotel.service.RoomUnavailableException;
import java.net.URI;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Every error as RFC 9457 problem details: a stable {@code type} the front end switches on and a
 * {@code detail} a person can read. Validation failures come from the base class with field errors.
 */
@RestControllerAdvice
class ApiErrors extends ResponseEntityExceptionHandler {

    static final String TYPES = "https://kestrel-vine.example/problems/";

    @ExceptionHandler
    ProblemDetail notFound(NotFoundException e) {
        return problem(HttpStatus.NOT_FOUND, "not-found", e.getMessage());
    }

    @ExceptionHandler
    ProblemDetail unavailable(RoomUnavailableException e) {
        return problem(HttpStatus.CONFLICT, "room-unavailable", e.getMessage());
    }

    @ExceptionHandler
    ProblemDetail rule(BookingRuleException e) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "booking-rule", e.getMessage());
    }

    /** The exclusion constraint caught a double booking the availability check let through. */
    @ExceptionHandler
    ProblemDetail race(DataIntegrityViolationException e) {
        return problem(HttpStatus.CONFLICT, "room-unavailable", "that room was just taken; search again");
    }

    private static ProblemDetail problem(
            HttpStatus status, String type, @org.jspecify.annotations.Nullable String detail) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, detail == null ? status.getReasonPhrase() : detail);
        problem.setType(URI.create(TYPES + type));
        return problem;
    }
}
