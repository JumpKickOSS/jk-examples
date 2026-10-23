package dev.jkexamples.hotel.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * The whole application over HTTP against PostgreSQL 18, migrated and seeded as in production:
 * search, book, find, cancel, the front desk, and two guests racing for the last room.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(Postgres.class)
class HotelApiIntegrationTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @LocalServerPort
    int port;

    /** Beyond the seeded six months, so every room is free. */
    private static final LocalDate IN = LocalDate.now().plusDays(200);

    @Test
    void a_guest_searches_books_finds_and_cancels() throws Exception {
        var api = new ApiClient(port);
        JsonNode results =
                JSON.readTree(api.get("/api/search?checkIn=" + IN + "&checkOut=" + IN.plusDays(3) + "&guests=2")
                        .body());
        assertThat(results.size()).isEqualTo(5);

        var booked = api.post("/api/reservations", booking("saltmeadow", "CTG", IN, IN.plusDays(3), "Okafor"));
        assertThat(booked.statusCode()).as(booked.body()).isEqualTo(201);
        String code = JSON.readTree(booked.body()).get("confirmationCode").asString();
        assertThat(code).startsWith("KV-");

        var found = api.post("/api/reservations/lookup", lookup(code, "okafor"));
        assertThat(JSON.readTree(found.body()).get("nights").asInt()).isEqualTo(3);
        assertThat(api.post("/api/reservations/lookup", lookup(code, "Someone")).statusCode())
                .isEqualTo(404);

        var cancelled = api.post("/api/reservations/cancel", lookup(code, "Okafor"));
        assertThat(JSON.readTree(cancelled.body()).get("status").asString()).isEqualTo("CANCELLED");
    }

    @Test
    void two_guests_racing_for_the_last_rooms_never_share_one() throws Exception {
        LocalDate in = IN.plusDays(30);
        var api = new ApiClient(port);
        JsonNode offers = JSON.readTree(api.get("/api/hotels/summit-house/availability?checkIn=" + in + "&checkOut="
                                + in.plusDays(2) + "&guests=2")
                        .body())
                .get("offers");
        JsonNode chalet = null;
        for (JsonNode offer : offers)
            if (offer.get("roomType").get("code").asString().equals("CHT")) chalet = offer;
        int rooms = chalet == null ? 0 : chalet.get("roomsLeft").asInt();
        assertThat(rooms).isPositive();

        List<Callable<Integer>> attempts = new ArrayList<>();
        for (int i = 0; i < rooms + 4; i++) {
            String name = "Racer" + i;
            attempts.add(() -> new ApiClient(port)
                    .post("/api/reservations", booking("summit-house", "CHT", in, in.plusDays(2), name))
                    .statusCode());
        }
        List<Integer> statuses = new ArrayList<>();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Future<Integer> f : pool.invokeAll(attempts)) statuses.add(f.get());
        }
        assertThat(statuses).filteredOn(s -> s == 201).hasSize(rooms);
        assertThat(statuses).filteredOn(s -> s == 409).hasSize(4);
    }

    @Test
    void the_front_desk_signs_in_and_sees_its_day() throws Exception {
        var desk = new ApiClient(port);
        assertThat(desk.get("/api/desk/me").statusCode()).isEqualTo(401);
        assertThat(desk.post("/api/auth/login", "username=desk&password=kestrel-desk")
                        .statusCode())
                .isEqualTo(204);

        JsonNode me = JSON.readTree(desk.get("/api/desk/me").body());
        assertThat(me.get("hotels").size()).isEqualTo(5);
        JsonNode day = JSON.readTree(
                desk.get("/api/desk/harborview/day?date=" + LocalDate.now()).body());
        assertThat(day.get("rooms").asInt()).isPositive();
        assertThat(day.get("occupancyPercent").asInt()).isBetween(1, 100);

        var harbor = new ApiClient(port);
        harbor.post("/api/auth/login", "username=harbor&password=kestrel-desk");
        assertThat(harbor.get("/api/desk/lantern-row/day?date=" + LocalDate.now())
                        .statusCode())
                .as("a hotel the user does not work is not found")
                .isEqualTo(404);
    }

    private static String booking(String hotel, String type, LocalDate in, LocalDate out, String lastName) {
        return """
                {"hotelSlug": "%s", "roomTypeCode": "%s", "checkIn": "%s", "checkOut": "%s", "adults": 2, "children": 0,
                 "guest": {"firstName": "Test", "lastName": "%s", "email": "%s@example.com", "phone": ""},
                 "specialRequests": ""}""".formatted(hotel, type, in, out, lastName, lastName.toLowerCase());
    }

    private static String lookup(String code, String lastName) {
        return "{\"confirmationCode\": \"%s\", \"lastName\": \"%s\"}".formatted(code, lastName);
    }
}
