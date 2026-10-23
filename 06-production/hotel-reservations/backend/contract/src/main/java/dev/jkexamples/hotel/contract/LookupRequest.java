package dev.jkexamples.hotel.contract;

import jakarta.validation.constraints.NotBlank;

/** What a guest proves they own a reservation with: its code and the last name on it. */
public record LookupRequest(
        @NotBlank String confirmationCode, @NotBlank String lastName) {}
