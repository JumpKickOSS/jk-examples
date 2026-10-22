package dev.jkexamples.hotel.contract;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Who is staying. */
public record GuestDetails(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 200) String email,
        @Pattern(regexp = "^[+0-9 ()-]{0,32}$") String phone) {}
