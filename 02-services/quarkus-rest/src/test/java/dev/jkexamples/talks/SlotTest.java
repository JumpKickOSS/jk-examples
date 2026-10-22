package dev.jkexamples.talks;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** The clash rule on its own: no container, no Quarkus, milliseconds. */
class SlotTest {

    private static final LocalDateTime NINE = LocalDateTime.of(2026, 11, 12, 9, 0);

    @Test
    void overlapping_talks_in_one_room_clash() {
        assertThat(new Slot("Harbour", NINE, 60).clashesWith(new Slot("harbour", NINE.plusMinutes(30), 30))).isTrue();
    }

    @Test
    void back_to_back_talks_do_not_clash() {
        assertThat(new Slot("Harbour", NINE, 45).clashesWith(new Slot("Harbour", NINE.plusMinutes(45), 30))).isFalse();
    }

    @Test
    void the_same_time_in_another_room_does_not_clash() {
        assertThat(new Slot("Harbour", NINE, 45).clashesWith(new Slot("Vine", NINE, 45))).isFalse();
    }
}
