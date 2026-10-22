package dev.jkexamples.hotel.contract;

import java.time.LocalTime;
import java.util.List;

/** One hotel with its room types, for the hotel page. */
public record HotelDetail(
        String slug,
        String name,
        String city,
        String country,
        int stars,
        String tagline,
        String description,
        String heroImage,
        List<String> gallery,
        List<String> amenities,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        List<RoomTypeView> roomTypes) {}
