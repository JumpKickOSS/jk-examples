package dev.jkexamples.hotel.contract;

import java.util.List;

/** A room type that is free for the requested stay, priced night by night. */
public record Offer(
        RoomTypeView roomType,
        int roomsLeft,
        List<NightlyPrice> nights,
        MoneyView subtotal,
        MoneyView discount,
        MoneyView taxes,
        MoneyView total) {}
