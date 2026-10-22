package dev.jkexamples.hotel.service;

import dev.jkexamples.hotel.contract.StaffUser;
import java.util.Optional;

/** Front-desk accounts, for sign-in. The password hash never leaves the backend. */
public interface StaffAccounts {

    /** A staff member and the hash sign-in checks the password against. */
    record Credentials(String username, String passwordHash) {}

    Optional<Credentials> credentials(String username);

    StaffUser user(String username);
}
