package dev.jkexamples.hotel.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.SplittableRandom;
import org.junit.jupiter.api.Test;

class ConfirmationCodesTest {

    @Test
    void codes_are_well_formed_and_avoid_confusable_characters() {
        var codes = new ConfirmationCodes(new SplittableRandom(7));
        for (int i = 0; i < 1_000; i++) {
            String code = codes.next();
            assertThat(ConfirmationCodes.isWellFormed(code)).as(code).isTrue();
            assertThat(code.substring(3)).doesNotContain("0", "O", "1", "I", "L");
        }
    }

    @Test
    void occupancy_rounds_half_up() {
        assertThat(new Occupancy(1, 3).percent()).isEqualTo(33);
        assertThat(new Occupancy(2, 3).percent()).isEqualTo(67);
        assertThat(new Occupancy(0, 0).percent()).isZero();
    }
}
