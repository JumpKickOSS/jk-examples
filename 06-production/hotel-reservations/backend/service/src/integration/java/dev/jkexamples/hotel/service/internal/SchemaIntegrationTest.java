package dev.jkexamples.hotel.service.internal;

import static dev.jkexamples.hotel.service.db.Tables.HOTEL;
import static dev.jkexamples.hotel.service.db.Tables.RESERVATION;
import static dev.jkexamples.hotel.service.db.Tables.ROOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.jkexamples.hotel.contract.GuestDetails;
import dev.jkexamples.hotel.core.Money;
import dev.jkexamples.hotel.core.Party;
import dev.jkexamples.hotel.core.StayDates;
import java.time.LocalDate;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;

/** The migrations against a real PostgreSQL 18: the schema, the seed, and the constraint that matters most. */
class SchemaIntegrationTest {

    private final DSLContext db = PostgresSchema.db();

    @Test
    void the_seed_gives_five_hotels_with_rooms_and_reservations_around_today() {
        assertThat(db.fetchCount(HOTEL)).isEqualTo(5);
        assertThat(db.fetchCount(ROOM)).isGreaterThan(100);
        assertThat(db.fetchCount(
                        RESERVATION,
                        RESERVATION.CHECK_IN.le(LocalDate.now()).and(RESERVATION.CHECK_OUT.gt(LocalDate.now()))))
                .as("guests in house tonight")
                .isGreaterThan(20);
    }

    @Test
    void the_database_refuses_a_second_live_booking_of_one_room_for_an_overlapping_night() {
        ReservationQueries reservations = new ReservationQueries(db);
        var room = db.selectFrom(ROOM).limit(1).fetchSingle();
        StayDates far =
                new StayDates(LocalDate.now().plusDays(400), LocalDate.now().plusDays(403));
        var guest = reservations.upsertGuest(new GuestDetails("Test", "Overlap", "overlap@example.com", ""));
        reservations.insert(
                "KV-TSTAA2", room.getHotelId(), room.getId(), guest, far, new Party(1, 0), new Money(1, "EUR"), "");

        StayDates overlapping =
                new StayDates(far.checkIn().plusDays(2), far.checkOut().plusDays(2));
        assertThatThrownBy(() -> reservations.insert(
                        "KV-TSTAA3",
                        room.getHotelId(),
                        room.getId(),
                        guest,
                        overlapping,
                        new Party(1, 0),
                        new Money(1, "EUR"),
                        ""))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("reservation_no_double_booking");

        StayDates backToBack = new StayDates(far.checkOut(), far.checkOut().plusDays(1));
        reservations.insert(
                "KV-TSTAA4",
                room.getHotelId(),
                room.getId(),
                guest,
                backToBack,
                new Party(1, 0),
                new Money(1, "EUR"),
                "");
    }

    @Test
    void a_room_is_free_until_a_live_reservation_covers_one_of_its_nights() {
        AvailabilityQueries availability = new AvailabilityQueries(db);
        var hotel = db.selectFrom(HOTEL).where(HOTEL.SLUG.eq("lantern-row")).fetchSingle();
        StayDates far =
                new StayDates(LocalDate.now().plusDays(500), LocalDate.now().plusDays(502));
        int roomsInHotel = db.fetchCount(ROOM, ROOM.HOTEL_ID.eq(hotel.getId()));
        assertThat(availability.freeRoomsByType(hotel.getId(), far).values().stream()
                        .mapToInt(Integer::intValue)
                        .sum())
                .isEqualTo(roomsInHotel);
    }
}
