package dev.jkexamples.hotel.service.internal;

import dev.jkexamples.hotel.contract.DeskDay;
import dev.jkexamples.hotel.contract.DeskReservation;
import dev.jkexamples.hotel.contract.OccupancyPoint;
import dev.jkexamples.hotel.core.Hotel;
import dev.jkexamples.hotel.core.Occupancy;
import dev.jkexamples.hotel.core.Reservation;
import dev.jkexamples.hotel.core.ReservationStatus;
import dev.jkexamples.hotel.service.BookingRuleException;
import dev.jkexamples.hotel.service.FrontDesk;
import dev.jkexamples.hotel.service.NotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The front desk's day. Every call is limited to the hotels the staff member works. */
@Service
@Transactional
class FrontDeskService implements FrontDesk {

    /** The longest occupancy chart one request may ask for. */
    static final int MAX_RANGE_DAYS = 92;

    private final HotelQueries hotels;
    private final ReservationQueries reservations;
    private final StaffQueries staff;
    private final ViewMapper views;

    FrontDeskService(HotelQueries hotels, ReservationQueries reservations, StaffQueries staff, ViewMapper views) {
        this.hotels = hotels;
        this.reservations = reservations;
        this.staff = staff;
        this.views = views;
    }

    @Override
    @Transactional(readOnly = true)
    public DeskDay day(String username, String hotelSlug, LocalDate date) {
        Hotel hotel = worked(username, hotelSlug);
        List<Reservation> tonight = reservations.overlapping(hotel.id(), date, date.plusDays(1));
        int rooms = reservations.rooms(hotel.id());
        return new DeskDay(
                hotel.slug(),
                hotel.name(),
                date,
                rooms,
                new Occupancy(tonight.size(), rooms).percent(),
                desk(reservations.arriving(hotel.id(), date)),
                desk(reservations.departing(hotel.id(), date)),
                desk(tonight.stream().filter(r -> r.status() == ReservationStatus.CHECKED_IN).toList()));
    }

    @Override
    public DeskReservation checkIn(String username, String confirmationCode) {
        return move(username, confirmationCode, ReservationStatus.CHECKED_IN);
    }

    @Override
    public DeskReservation checkOut(String username, String confirmationCode) {
        return move(username, confirmationCode, ReservationStatus.CHECKED_OUT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OccupancyPoint> occupancy(String username, String hotelSlug, LocalDate from, LocalDate to) {
        if (!to.isAfter(from) || ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new BookingRuleException("an occupancy range is 1 to " + MAX_RANGE_DAYS + " days");
        }
        Hotel hotel = worked(username, hotelSlug);
        int rooms = reservations.rooms(hotel.id());
        List<Reservation> live = reservations.overlapping(hotel.id(), from, to);
        List<OccupancyPoint> points = new ArrayList<>();
        for (LocalDate night = from; night.isBefore(to); night = night.plusDays(1)) {
            LocalDate n = night;
            int occupied = (int) live.stream().filter(r -> r.stay().includesNight(n)).count();
            points.add(new OccupancyPoint(night, occupied, rooms));
        }
        return points;
    }

    private DeskReservation move(String username, String code, ReservationStatus target) {
        Reservation reservation = reservations.byCode(code).orElseThrow(() -> new NotFoundException("no reservation " + code));
        worked(username, reservations.hotelSlug(reservation));
        try {
            reservations.setStatus(reservation.id(), reservation.status().moveTo(target));
        } catch (IllegalStateException e) {
            throw new BookingRuleException(e);
        }
        return views.desk(reservations.byCode(code).orElseThrow());
    }

    private Hotel worked(String username, String hotelSlug) {
        if (!staff.hotels(username).contains(hotelSlug)) {
            throw new NotFoundException("no hotel " + hotelSlug);
        }
        return hotels.bySlug(hotelSlug);
    }

    private List<DeskReservation> desk(List<Reservation> list) {
        return list.stream().map(views::desk).toList();
    }
}
