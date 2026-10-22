package dev.jkexamples.hotel.e2e;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import dev.jkexamples.hotel.rest.HotelApplication;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

/**
 * The product through a real browser: the Angular app served by the Boot jar, on PostgreSQL 18,
 * driven by Playwright's Chromium. A guest books, finds and cancels a stay; the desk checks someone in.
 */
@SpringBootTest(classes = HotelApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(E2ePostgres.class)
class GuestJourneyE2eTest {

    private static Playwright playwright;
    private static Browser browser;

    @LocalServerPort
    int port;

    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void browser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @AfterAll
    static void close() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void page() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closePage() {
        context.close();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void a_guest_books_a_room_finds_the_booking_and_cancels_it() {
        LocalDate in = LocalDate.now().plusDays(210);
        page.navigate(url("/"));
        assertThat(page.getByTestId("hotel-saltmeadow")).isVisible();

        page.navigate(url("/hotels/saltmeadow?checkIn=" + in + "&checkOut=" + in.plusDays(3) + "&guests=2"));
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Kestrel & Vine Saltmeadow Lodge"))).isVisible();
        page.getByTestId("offer-CTG").getByTestId("choose-room").click();

        page.getByTestId("first-name").fill("Ines");
        page.getByTestId("last-name").fill("Quintero");
        page.getByTestId("email").fill("ines.e2e@example.com");
        page.getByTestId("to-review").click();
        page.getByTestId("confirm-booking").click();

        assertThat(page.getByText("You're booked.")).isVisible();
        String code = page.getByTestId("confirmation-code").innerText().strip();

        page.navigate(url("/reservation"));
        page.getByTestId("lookup-code").fill(code);
        page.getByTestId("lookup-last-name").fill("quintero");
        page.getByTestId("lookup").click();
        assertThat(page.getByTestId("status")).hasText("Booked");

        page.getByTestId("cancel").click();
        page.getByTestId("confirm-cancel").click();
        assertThat(page.getByTestId("status")).hasText("Cancelled");
    }

    @Test
    void the_front_desk_signs_in_and_checks_a_guest_in() {
        page.navigate(url("/desk"));
        assertThat(page).hasURL(url("/desk/login"));
        page.getByTestId("desk-username").fill("desk");
        page.getByTestId("desk-password").fill("kestrel-desk");
        page.getByTestId("desk-sign-in").click();

        assertThat(page.getByTestId("kpi-occupancy")).containsText("%");
        var firstCheckIn = page.locator("[data-testid^=check-in-]").first();
        if (firstCheckIn.count() > 0) {
            String testId = java.util.Objects.requireNonNull(firstCheckIn.getAttribute("data-testid"));
            firstCheckIn.click();
            assertThat(page.locator("[data-testid=" + testId + "]")).hasCount(0);
        }
    }
}
