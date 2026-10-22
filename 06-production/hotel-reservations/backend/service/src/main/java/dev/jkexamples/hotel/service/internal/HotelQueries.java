package dev.jkexamples.hotel.service.internal;

import static dev.jkexamples.hotel.service.db.Tables.HOTEL;
import static dev.jkexamples.hotel.service.db.Tables.ROOM_TYPE;

import dev.jkexamples.hotel.core.Hotel;
import dev.jkexamples.hotel.core.RoomType;
import dev.jkexamples.hotel.service.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

/** Hotels and room types. */
@Repository
class HotelQueries {

    private final DSLContext db;

    HotelQueries(DSLContext db) {
        this.db = db;
    }

    List<Hotel> all() {
        return db.selectFrom(HOTEL).orderBy(HOTEL.NAME).fetch(Rows::hotel);
    }

    Hotel bySlug(String slug) {
        return db.selectFrom(HOTEL)
                .where(HOTEL.SLUG.eq(slug))
                .fetchOptional(Rows::hotel)
                .orElseThrow(() -> new NotFoundException("no hotel " + slug));
    }

    Hotel byId(UUID id) {
        return db.selectFrom(HOTEL).where(HOTEL.ID.eq(id)).fetchSingle(Rows::hotel);
    }

    List<RoomType> roomTypes(Hotel hotel) {
        return db.selectFrom(ROOM_TYPE)
                .where(ROOM_TYPE.HOTEL_ID.eq(hotel.id()))
                .orderBy(ROOM_TYPE.BASE_RATE_MINOR)
                .fetch(r -> Rows.roomType(r, hotel.currency()));
    }

    Optional<RoomType> roomType(Hotel hotel, String code) {
        return db.selectFrom(ROOM_TYPE)
                .where(ROOM_TYPE.HOTEL_ID.eq(hotel.id()), ROOM_TYPE.CODE.eq(code))
                .fetchOptional(r -> Rows.roomType(r, hotel.currency()));
    }
}
