package dev.jkexamples.hotel.service.internal;

import static dev.jkexamples.hotel.service.db.Tables.GUEST;
import static dev.jkexamples.hotel.service.db.Tables.HOTEL;
import static dev.jkexamples.hotel.service.db.Tables.RESERVATION;
import static dev.jkexamples.hotel.service.db.Tables.ROOM;
import static dev.jkexamples.hotel.service.db.Tables.ROOM_TYPE;

import dev.jkexamples.hotel.contract.GuestDetails;
import dev.jkexamples.hotel.core.Money;
import dev.jkexamples.hotel.core.Party;
import dev.jkexamples.hotel.core.Reservation;
import dev.jkexamples.hotel.core.ReservationStatus;
import dev.jkexamples.hotel.core.StayDates;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.springframework.stereotype.Repository;

/** Reservations with their guest, room and room type. */
@Repository
class ReservationQueries {

    private final DSLContext db;

    ReservationQueries(DSLContext db) {
        this.db = db;
    }

    /** The guest with this email, created or brought up to date with the name and phone given. */
    UUID upsertGuest(GuestDetails guest) {
        return db.insertInto(GUEST)
                .set(GUEST.FIRST_NAME, guest.firstName().strip())
                .set(GUEST.LAST_NAME, guest.lastName().strip())
                .set(GUEST.EMAIL, guest.email().strip().toLowerCase(java.util.Locale.ROOT))
                .set(GUEST.PHONE, guest.phone().strip())
                .onConflict(GUEST.EMAIL)
                .doUpdate()
                .set(GUEST.FIRST_NAME, guest.firstName().strip())
                .set(GUEST.LAST_NAME, guest.lastName().strip())
                .set(GUEST.PHONE, guest.phone().strip())
                .returning(GUEST.ID)
                .fetchSingle()
                .getId();
    }

    void insert(String code, UUID hotelId, UUID roomId, UUID guestId, StayDates stay, Party party, Money total, String requests) {
        db.insertInto(RESERVATION)
                .set(RESERVATION.CONFIRMATION_CODE, code)
                .set(RESERVATION.HOTEL_ID, hotelId)
                .set(RESERVATION.ROOM_ID, roomId)
                .set(RESERVATION.GUEST_ID, guestId)
                .set(RESERVATION.CHECK_IN, stay.checkIn())
                .set(RESERVATION.CHECK_OUT, stay.checkOut())
                .set(RESERVATION.ADULTS, (short) party.adults())
                .set(RESERVATION.CHILDREN, (short) party.children())
                .set(RESERVATION.STATUS, ReservationStatus.BOOKED.name())
                .set(RESERVATION.TOTAL_MINOR, total.minor())
                .set(RESERVATION.CURRENCY, total.currency())
                .set(RESERVATION.SPECIAL_REQUESTS, requests.strip())
                .execute();
    }

    Optional<Reservation> byCode(String code) {
        return select().where(RESERVATION.CONFIRMATION_CODE.eq(code)).fetchOptional(Rows::reservation);
    }

    /** The slug of the hotel a reservation is at. */
    String hotelSlug(Reservation reservation) {
        return db.select(HOTEL.SLUG).from(HOTEL).where(HOTEL.ID.eq(reservation.hotelId())).fetchSingle().value1();
    }

    void setStatus(UUID id, ReservationStatus status) {
        db.update(RESERVATION).set(RESERVATION.STATUS, status.name()).where(RESERVATION.ID.eq(id)).execute();
    }

    List<Reservation> arriving(UUID hotelId, LocalDate date) {
        return select()
                .where(RESERVATION.HOTEL_ID.eq(hotelId), RESERVATION.CHECK_IN.eq(date), RESERVATION.STATUS.ne("CANCELLED"))
                .orderBy(ROOM.NUMBER)
                .fetch(Rows::reservation);
    }

    List<Reservation> departing(UUID hotelId, LocalDate date) {
        return select()
                .where(RESERVATION.HOTEL_ID.eq(hotelId), RESERVATION.CHECK_OUT.eq(date), RESERVATION.STATUS.ne("CANCELLED"))
                .orderBy(ROOM.NUMBER)
                .fetch(Rows::reservation);
    }

    /** Live reservations of one hotel with at least one night in {@code [from, to)}. */
    List<Reservation> overlapping(UUID hotelId, LocalDate from, LocalDate to) {
        return select()
                .where(RESERVATION.HOTEL_ID.eq(hotelId),
                        RESERVATION.STATUS.ne("CANCELLED"),
                        RESERVATION.CHECK_IN.lt(to),
                        RESERVATION.CHECK_OUT.gt(from))
                .orderBy(ROOM.NUMBER)
                .fetch(Rows::reservation);
    }

    int rooms(UUID hotelId) {
        return db.fetchCount(ROOM, ROOM.HOTEL_ID.eq(hotelId));
    }

    private SelectOnConditionStep<Record> select() {
        return db.select()
                .from(RESERVATION)
                .join(GUEST).on(GUEST.ID.eq(RESERVATION.GUEST_ID))
                .join(ROOM).on(ROOM.ID.eq(RESERVATION.ROOM_ID))
                .join(ROOM_TYPE).on(ROOM_TYPE.ID.eq(ROOM.ROOM_TYPE_ID));
    }
}
