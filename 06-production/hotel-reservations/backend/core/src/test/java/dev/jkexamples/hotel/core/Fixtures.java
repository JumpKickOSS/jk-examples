package dev.jkexamples.hotel.core;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/** A hotel and a room type for rules that need one. */
final class Fixtures {

    static final Hotel HARBOR = new Hotel(
            UUID.fromString("01900000-0000-7000-8000-000000000001"),
            "harborview", "Kestrel & Vine Harborview", "Port Ellery", "Eastmarch",
            ZoneId.of("Europe/Lisbon"), 5, "Tide-washed light", "", "harborview.avif",
            List.of(), List.of("Pool"), LocalTime.of(15, 0), LocalTime.of(11, 0), "EUR", 1200);

    static final RoomType KING = new RoomType(
            UUID.fromString("01900000-0000-7000-8000-000000000101"), HARBOR.id(), "KING", "Harbour King",
            "", 2, "1 king", 32, "king.avif", new Money(20_000, "EUR"), List.of());

    private Fixtures() {}
}
