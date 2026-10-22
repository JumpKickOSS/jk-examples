package dev.jkexamples.hotel.rest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.jkexamples.hotel.service.Bookings;
import dev.jkexamples.hotel.service.FrontDesk;
import dev.jkexamples.hotel.service.HotelCatalog;
import dev.jkexamples.hotel.service.StaffAccounts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Booking requests are validated at the edge and must carry the CSRF token. */
@WebMvcTest
@Import({SecurityConfiguration.class, LoginRateLimit.class, TestClock.class})
class ReservationControllerTest {

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
    void an_invalid_booking_is_a_400_problem_naming_the_fields() throws Exception {
        mvc.perform(post("/api/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hotelSlug": "harborview", "roomTypeCode": "HKG",
                                 "checkIn": "2020-01-01", "checkOut": "2020-01-03", "adults": 0, "children": 0,
                                 "guest": {"firstName": "", "lastName": "Abbott", "email": "not-an-email", "phone": ""},
                                 "specialRequests": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void a_post_without_the_csrf_token_is_refused() throws Exception {
        mvc.perform(post("/api/reservations/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmationCode\": \"KV-ABC234\", \"lastName\": \"Abbott\"}"))
                .andExpect(status().isForbidden());
    }
}
