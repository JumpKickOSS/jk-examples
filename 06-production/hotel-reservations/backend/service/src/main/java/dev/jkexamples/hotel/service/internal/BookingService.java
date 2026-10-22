package dev.jkexamples.hotel.service.internal;

import dev.jkexamples.hotel.contract.BookingRequest;
import dev.jkexamples.hotel.contract.LookupRequest;
import dev.jkexamples.hotel.contract.ReservationView;
import dev.jkexamples.hotel.core.ConfirmationCodes;
import dev.jkexamples.hotel.core.Hotel;
import dev.jkexamples.hotel.core.InvalidStayException;
import dev.jkexamples.hotel.core.Party;
import dev.jkexamples.hotel.core.PricingPolicy;
import dev.jkexamples.hotel.core.Quote;
import dev.jkexamples.hotel.core.Reservation;
import dev.jkexamples.hotel.core.ReservationStatus;
import dev.jkexamples.hotel.core.RoomType;
import dev.jkexamples.hotel.core.StayDates;
import dev.jkexamples.hotel.service.BookingRuleException;
import dev.jkexamples.hotel.service.Bookings;
import dev.jkexamples.hotel.service.NotFoundException;
import dev.jkexamples.hotel.service.RoomUnavailableException;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Booking, finding and cancelling as a guest. */
@Service
@Transactional
class BookingService implements Bookings {

    private final HotelQueries hotels;
    private final AvailabilityQueries availability;
    private final ReservationQueries reservations;
    private final PricingPolicy pricing;
    private final ConfirmationCodes codes;
    private final ViewMapper views;
    private final Clock clock;

    BookingService(
            HotelQueries hotels,
            AvailabilityQueries availability,
            ReservationQueries reservations,
            PricingPolicy pricing,
            ConfirmationCodes codes,
            ViewMapper views,
            Clock clock) {
        this.hotels = hotels;
        this.availability = availability;
        this.reservations = reservations;
        this.pricing = pricing;
        this.codes = codes;
        this.views = views;
        this.clock = clock;
    }

    @Override
    public ReservationView book(BookingRequest request) {
        Hotel hotel = hotels.bySlug(request.hotelSlug());
        RoomType type = hotels.roomType(hotel, request.roomTypeCode())
                .orElseThrow(() -> new NotFoundException("no room type " + request.roomTypeCode() + " at " + hotel.name()));
        StayDates stay = CatalogService.stay(request.checkIn(), request.checkOut());
        if (stay.checkIn().isBefore(today(hotel))) throw new BookingRuleException("check-in is in the past");
        Party party = CatalogService.party(request.adults(), request.children());
        try {
            type.admit(party);
        } catch (InvalidStayException e) {
            throw new BookingRuleException(e);
        }
        var room = availability.claimFreeRoom(type.id(), stay)
                .orElseThrow(() -> new RoomUnavailableException(type.name() + " is sold out for those dates"));
        Quote quote = pricing.quote(hotel, type, stay);
        String code = codes.next();
        var guestId = reservations.upsertGuest(request.guest());
        reservations.insert(code, hotel.id(), room.id(), guestId, stay, party, quote.total(), request.specialRequests());
        return view(reservations.byCode(code).orElseThrow(), hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationView find(LookupRequest lookup) {
        Reservation reservation = owned(lookup);
        return view(reservation, hotels.byId(reservation.hotelId()));
    }

    @Override
    public ReservationView cancel(LookupRequest lookup) {
        Reservation reservation = owned(lookup);
        Hotel hotel = hotels.byId(reservation.hotelId());
        if (!reservation.cancellableOn(today(hotel))) {
            throw new BookingRuleException("a " + reservation.status().name().toLowerCase(java.util.Locale.ROOT)
                    + " reservation can no longer be cancelled online; call the hotel");
        }
        reservations.setStatus(reservation.id(), reservation.status().moveTo(ReservationStatus.CANCELLED));
        return view(reservations.byCode(reservation.confirmationCode()).orElseThrow(), hotel);
    }

    /** The reservation, if the code exists and the last name matches; the same answer either way otherwise. */
    private Reservation owned(LookupRequest lookup) {
        String code = lookup.confirmationCode().strip().toUpperCase(java.util.Locale.ROOT);
        return reservations.byCode(code)
                .filter(r -> r.belongsTo(lookup.lastName()))
                .orElseThrow(() -> new NotFoundException("no reservation " + code + " under that name"));
    }

    private ReservationView view(Reservation reservation, Hotel hotel) {
        return views.reservation(reservation, hotel, reservation.cancellableOn(today(hotel)));
    }

    private LocalDate today(Hotel hotel) {
        return LocalDate.now(clock.withZone(hotel.zone()));
    }
}
