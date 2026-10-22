package dev.jkexamples.hotel.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** A request to book one room of a type for a stay. */
public record BookingRequest(
        @NotBlank String hotelSlug,
        @NotBlank String roomTypeCode,
        @NotNull @FutureOrPresent LocalDate checkIn,
        @NotNull @Future LocalDate checkOut,
        @Min(1) @Max(8) int adults,
        @Min(0) @Max(6) int children,
        @NotNull @Valid GuestDetails guest,
        @Size(max = 500) String specialRequests) {}
