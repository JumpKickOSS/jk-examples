package dev.jkexamples.hotel.contract;

import jakarta.validation.constraints.NotBlank;

/** A front-desk sign-in. */
public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
