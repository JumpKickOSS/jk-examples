package dev.jkexamples.hotel.contract;

import java.util.List;

/** The signed-in front-desk user. */
public record StaffUser(String username, String displayName, List<String> hotels) {}
