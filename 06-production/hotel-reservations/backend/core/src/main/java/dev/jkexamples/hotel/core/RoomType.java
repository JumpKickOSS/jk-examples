package dev.jkexamples.hotel.core;

import java.util.List;
import java.util.UUID;

/** A kind of room one hotel sells, priced from {@code baseRate} per night. */
public record RoomType(
        UUID id,
        UUID hotelId,
        String code,
        String name,
        String description,
        int maxGuests,
        String beds,
        int sizeSqm,
        String image,
        Money baseRate,
        List<String> features) {

    public void admit(Party party) {
        if (party.size() > maxGuests) {
            throw new InvalidStayException(name + " sleeps at most " + maxGuests);
        }
    }
}
