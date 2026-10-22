package dev.jkexamples.hotel.core;

import java.util.UUID;

/** A person who books. Email is the guest's identity across stays. */
public record Guest(UUID id, String firstName, String lastName, String email, String phone) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}
