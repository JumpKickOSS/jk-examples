package dev.jkexamples.hotel.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ReservationStatusTest {

    @Test
    void a_booking_is_checked_in_then_out() {
        assertThat(ReservationStatus.BOOKED.moveTo(ReservationStatus.CHECKED_IN).moveTo(ReservationStatus.CHECKED_OUT))
                .isEqualTo(ReservationStatus.CHECKED_OUT);
    }

    @Test
    void a_guest_in_house_cannot_be_cancelled() {
        assertThatThrownBy(() -> ReservationStatus.CHECKED_IN.moveTo(ReservationStatus.CANCELLED))
                .isInstanceOf(IllegalStateException.class);
    }
}
