package dev.jkexamples.hotel.contract;

import java.util.List;

/** A kind of room a hotel sells. */
public record RoomTypeView(
        String code,
        String name,
        String description,
        int maxGuests,
        String beds,
        int sizeSqm,
        String image,
        MoneyView baseRate,
        List<String> features) {}
