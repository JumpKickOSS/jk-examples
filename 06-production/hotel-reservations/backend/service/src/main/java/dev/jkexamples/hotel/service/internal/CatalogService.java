package dev.jkexamples.hotel.service.internal;

import dev.jkexamples.hotel.contract.Availability;
import dev.jkexamples.hotel.contract.HotelDetail;
import dev.jkexamples.hotel.contract.HotelSummary;
import dev.jkexamples.hotel.contract.Offer;
import dev.jkexamples.hotel.contract.SearchResult;
import dev.jkexamples.hotel.core.Hotel;
import dev.jkexamples.hotel.core.InvalidStayException;
import dev.jkexamples.hotel.core.Money;
import dev.jkexamples.hotel.core.Party;
import dev.jkexamples.hotel.core.PricingPolicy;
import dev.jkexamples.hotel.core.RoomType;
import dev.jkexamples.hotel.core.StayDates;
import dev.jkexamples.hotel.service.BookingRuleException;
import dev.jkexamples.hotel.service.HotelCatalog;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Hotels, their rooms, and priced offers for a stay. */
@Service
@Transactional(readOnly = true)
class CatalogService implements HotelCatalog {

    private final HotelQueries hotels;
    private final AvailabilityQueries availability;
    private final PricingPolicy pricing;
    private final ViewMapper views;

    CatalogService(HotelQueries hotels, AvailabilityQueries availability, PricingPolicy pricing, ViewMapper views) {
        this.hotels = hotels;
        this.availability = availability;
        this.pricing = pricing;
        this.views = views;
    }

    @Override
    public List<HotelSummary> hotels() {
        return hotels.all().stream().map(this::summary).toList();
    }

    @Override
    public HotelDetail hotel(String slug) {
        Hotel hotel = hotels.bySlug(slug);
        return views.detail(hotel, hotels.roomTypes(hotel));
    }

    @Override
    public List<SearchResult> search(LocalDate checkIn, LocalDate checkOut, int guests) {
        StayDates stay = stay(checkIn, checkOut);
        List<SearchResult> results = new ArrayList<>();
        for (Hotel hotel : hotels.all()) {
            offers(hotel, stay, guests).stream()
                    .map(Offer::total)
                    .min(Comparator.comparingLong(m -> m.minor()))
                    .ifPresent(lowest -> results.add(new SearchResult(summary(hotel), lowest, stay.nights())));
        }
        results.sort(Comparator.comparingLong(r -> r.lowestTotal().minor()));
        return results;
    }

    @Override
    public Availability availability(String slug, LocalDate checkIn, LocalDate checkOut, int guests) {
        StayDates stay = stay(checkIn, checkOut);
        Hotel hotel = hotels.bySlug(slug);
        return new Availability(slug, checkIn, checkOut, guests, offers(hotel, stay, guests));
    }

    private List<Offer> offers(Hotel hotel, StayDates stay, int guests) {
        Map<java.util.UUID, Integer> free = availability.freeRoomsByType(hotel.id(), stay);
        List<Offer> offers = new ArrayList<>();
        for (RoomType type : hotels.roomTypes(hotel)) {
            int left = free.getOrDefault(type.id(), 0);
            if (left == 0 || type.maxGuests() < guests) continue;
            offers.add(views.offer(type, left, pricing.quote(hotel, type, stay)));
        }
        return offers;
    }

    private HotelSummary summary(Hotel hotel) {
        Money from = hotels.roomTypes(hotel).stream()
                .map(RoomType::baseRate)
                .min(Comparator.comparingLong(Money::minor))
                .orElse(Money.zero(hotel.currency()));
        return views.summary(hotel, from);
    }

    static StayDates stay(LocalDate checkIn, LocalDate checkOut) {
        try {
            return new StayDates(checkIn, checkOut);
        } catch (InvalidStayException e) {
            throw new BookingRuleException(e);
        }
    }

    static Party party(int adults, int children) {
        try {
            return new Party(adults, children);
        } catch (InvalidStayException e) {
            throw new BookingRuleException(e);
        }
    }
}
