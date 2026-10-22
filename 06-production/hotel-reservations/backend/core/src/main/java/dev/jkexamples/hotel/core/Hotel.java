package dev.jkexamples.hotel.core;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/** A property. {@code taxBasisPoints} is the local lodging tax on the room price. */
public record Hotel(
        UUID id,
        String slug,
        String name,
        String city,
        String country,
        ZoneId zone,
        int stars,
        String tagline,
        String description,
        String heroImage,
        List<String> gallery,
        List<String> amenities,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String currency,
        int taxBasisPoints) {}
