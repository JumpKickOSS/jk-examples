package dev.jkexamples.hotel.service.internal;

import static dev.jkexamples.hotel.service.db.Tables.RESERVATION;
import static dev.jkexamples.hotel.service.db.Tables.ROOM;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.selectOne;

import dev.jkexamples.hotel.core.StayDates;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

/** Which rooms are free for a stay: no live reservation on the room overlaps any of its nights. */
@Repository
class AvailabilityQueries {

    /** A free room and its number. */
    record FreeRoom(UUID id, String number) {}

    private final DSLContext db;

    AvailabilityQueries(DSLContext db) {
        this.db = db;
    }

    /** Free rooms per room type of one hotel. A type with none free is absent. */
    Map<UUID, Integer> freeRoomsByType(UUID hotelId, StayDates stay) {
        return db.select(ROOM.ROOM_TYPE_ID, count())
                .from(ROOM)
                .where(ROOM.HOTEL_ID.eq(hotelId), free(stay))
                .groupBy(ROOM.ROOM_TYPE_ID)
                .fetchMap(ROOM.ROOM_TYPE_ID, count());
    }

    /**
     * Lock one free room of a type for this transaction. {@code SKIP LOCKED} lets two concurrent
     * bookings take two different rooms instead of waiting on the same one; the exclusion constraint
     * still refuses an overlap if both somehow pick one room.
     */
    Optional<FreeRoom> claimFreeRoom(UUID roomTypeId, StayDates stay) {
        return db.select(ROOM.ID, ROOM.NUMBER)
                .from(ROOM)
                .where(ROOM.ROOM_TYPE_ID.eq(roomTypeId), free(stay))
                .orderBy(ROOM.NUMBER)
                .limit(1)
                .forUpdate()
                .skipLocked()
                .fetchOptional(r -> new FreeRoom(r.value1(), r.value2()));
    }

    private static Condition free(StayDates stay) {
        return notExists(selectOne()
                .from(RESERVATION)
                .where(
                        RESERVATION.ROOM_ID.eq(ROOM.ID),
                        RESERVATION.STATUS.ne("CANCELLED"),
                        RESERVATION.CHECK_IN.lt(stay.checkOut()),
                        RESERVATION.CHECK_OUT.gt(stay.checkIn())));
    }
}
