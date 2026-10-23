package dev.jkexamples.hotel.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class StayDatesTest {

    private static StayDates stay(String in, String out) {
        return new StayDates(LocalDate.parse(in), LocalDate.parse(out));
    }

    @Test
    void check_out_is_not_a_night() {
        assertThat(stay("2026-12-30", "2027-01-02").nights()).isEqualTo(3);
        assertThat(stay("2026-12-30", "2027-01-02").includesNight(LocalDate.parse("2027-01-02")))
                .isFalse();
    }

    @Test
    void back_to_back_stays_do_not_overlap() {
        assertThat(stay("2026-12-01", "2026-12-04").overlaps(stay("2026-12-04", "2026-12-06")))
                .isFalse();
        assertThat(stay("2026-12-01", "2026-12-05").overlaps(stay("2026-12-04", "2026-12-06")))
                .isTrue();
    }

    @Test
    void backwards_and_overlong_stays_are_refused() {
        assertThatThrownBy(() -> stay("2026-12-04", "2026-12-04")).isInstanceOf(InvalidStayException.class);
        assertThatThrownBy(() -> stay("2026-12-01", "2027-01-15")).hasMessageContaining("30 nights");
    }

    @Test
    void a_room_admits_no_more_guests_than_it_sleeps() {
        assertThatThrownBy(() -> Fixtures.KING.admit(new Party(2, 1))).hasMessageContaining("at most 2");
    }
}
