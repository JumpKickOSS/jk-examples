package dev.jkexamples.hotel.rest;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.jkexamples.hotel.contract.HotelSummary;
import dev.jkexamples.hotel.contract.MoneyView;
import dev.jkexamples.hotel.service.Bookings;
import dev.jkexamples.hotel.service.FrontDesk;
import dev.jkexamples.hotel.service.HotelCatalog;
import dev.jkexamples.hotel.service.NotFoundException;
import dev.jkexamples.hotel.service.StaffAccounts;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** The HTTP shape of the public API, over a mocked service layer: fast, no database. */
@WebMvcTest
@Import({SecurityConfiguration.class, LoginRateLimit.class, TestClock.class})
class CatalogControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    HotelCatalog catalog;

    @MockitoBean
    Bookings bookings;

    @MockitoBean
    FrontDesk desk;

    @MockitoBean
    StaffAccounts staff;

    @Test
    void hotels_are_listed_with_security_headers() throws Exception {
        given(catalog.hotels()).willReturn(List.of(new HotelSummary(
                "harborview", "Kestrel & Vine Harborview", "Port Ellery", "Aurelian Coast", 5, "Harbour light",
                "/hero.webp", new MoneyView(19_000, "EUR"), List.of("Spa"))));

        mvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("harborview"))
                .andExpect(jsonPath("$[0].fromPrice.minor").value(19_000))
                .andExpect(header().string("Content-Security-Policy", SecurityConfiguration.CSP));
    }

    @Test
    void an_unknown_hotel_is_a_404_problem() throws Exception {
        given(catalog.hotel("nowhere")).willThrow(new NotFoundException("no hotel nowhere"));

        mvc.perform(get("/api/hotels/nowhere"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value(ApiErrors.TYPES + "not-found"))
                .andExpect(jsonPath("$.detail").value("no hotel nowhere"));
    }

    @Test
    void the_front_desk_needs_a_signed_in_user() throws Exception {
        mvc.perform(get("/api/desk/me")).andExpect(status().isUnauthorized());
    }
}
