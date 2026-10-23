package dev.jkexamples.hotel.service.internal;

import static dev.jkexamples.hotel.service.db.Tables.GUEST;
import static dev.jkexamples.hotel.service.db.Tables.HOTEL;
import static dev.jkexamples.hotel.service.db.Tables.RESERVATION;
import static dev.jkexamples.hotel.service.db.Tables.ROOM;
import static dev.jkexamples.hotel.service.db.Tables.ROOM_TYPE;

import dev.jkexamples.hotel.core.Guest;
import dev.jkexamples.hotel.core.Hotel;
import dev.jkexamples.hotel.core.Money;
import dev.jkexamples.hotel.core.Party;
import dev.jkexamples.hotel.core.Reservation;
import dev.jkexamples.hotel.core.ReservationStatus;
import dev.jkexamples.hotel.core.RoomType;
import dev.jkexamples.hotel.core.StayDates;
import dev.jkexamples.hotel.service.db.tables.records.HotelRecord;
import dev.jkexamples.hotel.service.db.tables.records.RoomTypeRecord;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import org.jooq.Record;

/** Database rows to domain objects. jOOQ's records are the "entities"; nothing outside reads them. */
final class Rows {

    private Rows() {}

    static Hotel hotel(HotelRecord r) {
        return new Hotel(
                r.getId(),
                r.getSlug(),
                r.getName(),
                r.getCity(),
                r.getCountry(),
                ZoneId.of(r.getTimeZone()),
                r.getStars(),
                r.getTagline(),
                r.getDescription(),
                r.getHeroImage(),
                List.of(r.getGallery()),
                List.of(r.getAmenities()),
                r.getCheckInTime(),
                r.getCheckOutTime(),
                r.getCurrency(),
                r.getTaxBp());
    }

    static RoomType roomType(RoomTypeRecord r, String currency) {
        return new RoomType(
                r.getId(),
                r.getHotelId(),
                r.getCode(),
                r.getName(),
                r.getDescription(),
                r.getMaxGuests(),
                r.getBeds(),
                r.getSizeSqm(),
                r.getImage(),
                new Money(r.getBaseRateMinor(), currency),
                List.of(r.getFeatures()));
    }

    /** A reservation joined with its guest, room and room type (see {@link ReservationQueries#select}). */
    static Reservation reservation(Record r) {
        return new Reservation(
                r.get(RESERVATION.ID),
                r.get(RESERVATION.CONFIRMATION_CODE),
                r.get(RESERVATION.HOTEL_ID),
                r.get(RESERVATION.ROOM_ID),
                r.get(ROOM.NUMBER),
                r.get(ROOM_TYPE.ID),
                r.get(ROOM_TYPE.NAME),
                new Guest(
                        r.get(GUEST.ID),
                        r.get(GUEST.FIRST_NAME),
                        r.get(GUEST.LAST_NAME),
                        r.get(GUEST.EMAIL),
                        r.get(GUEST.PHONE)),
                new StayDates(r.get(RESERVATION.CHECK_IN), r.get(RESERVATION.CHECK_OUT)),
                new Party(r.get(RESERVATION.ADULTS), r.get(RESERVATION.CHILDREN)),
                ReservationStatus.valueOf(r.get(RESERVATION.STATUS)),
                new Money(r.get(RESERVATION.TOTAL_MINOR), r.get(RESERVATION.CURRENCY)),
                r.get(RESERVATION.SPECIAL_REQUESTS),
                Objects.requireNonNull(r.get(RESERVATION.BOOKED_AT)).toInstant());
    }

    static String hotelSlug(Record r) {
        return r.get(HOTEL.SLUG);
    }
}
