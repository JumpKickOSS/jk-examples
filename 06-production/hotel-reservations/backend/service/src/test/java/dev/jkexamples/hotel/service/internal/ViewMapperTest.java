package dev.jkexamples.hotel.service.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dev.jkexamples.hotel.contract.MoneyView;
import dev.jkexamples.hotel.contract.ReservationView;
import dev.jkexamples.hotel.core.Guest;
import dev.jkexamples.hotel.core.Hotel;
import dev.jkexamples.hotel.core.Money;
import dev.jkexamples.hotel.core.Party;
import dev.jkexamples.hotel.core.Reservation;
import dev.jkexamples.hotel.core.ReservationStatus;
import dev.jkexamples.hotel.core.StayDates;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** The generated mapper fills every contract field from the domain, by name or by rule. */
class ViewMapperTest {

    private final ViewMapper views = new ViewMapperImpl();

    private static final Hotel HOTEL = new Hotel(
            UUID.randomUUID(), "harborview", "Kestrel & Vine Harborview", "Port Ellery", "Aurelian Coast",
            ZoneId.of("Europe/Lisbon"), 5, "Harbour light", "…", "/hero.webp", List.of(), List.of("Spa"),
            LocalTime.of(15, 0), LocalTime.of(11, 0), "EUR", 1200);

    @Test
    void a_reservation_view_carries_the_stay_the_guest_and_the_hotel() {
        Reservation r = new Reservation(
                UUID.randomUUID(), "KV-ABC234", HOTEL.id(), UUID.randomUUID(), "204", UUID.randomUUID(), "Harbour King",
                new Guest(UUID.randomUUID(), "Ada", "Abbott", "ada.abbott@example.com", ""),
                new StayDates(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 4)),
                new Party(2, 0), ReservationStatus.BOOKED, new Money(80_640, "EUR"), "late arrival",
                Instant.parse("2026-10-01T10:00:00Z"));

        ReservationView view = views.reservation(r, HOTEL, true);

        assertThat(view.confirmationCode()).isEqualTo("KV-ABC234");
        assertThat(view.status()).isEqualTo("BOOKED");
        assertThat(view.hotelName()).isEqualTo("Kestrel & Vine Harborview");
        assertThat(view.nights()).isEqualTo(3);
        assertThat(view.guestName()).isEqualTo("Ada Abbott");
        assertThat(view.total()).isEqualTo(new MoneyView(80_640, "EUR"));
        assertThat(view.cancellable()).isTrue();
    }

    @Test
    void a_hotel_summary_takes_its_from_price_from_the_second_source() {
        assertThat(views.summary(HOTEL, new Money(19_000, "EUR")).fromPrice()).isEqualTo(new MoneyView(19_000, "EUR"));
    }
}
