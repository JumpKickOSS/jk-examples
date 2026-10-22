package dev.jkexamples.wordfreq.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CounterTest {

    @Test
    void most_frequent_first_case_folded() {
        var top = Counter.top("The cat and the hat. THE end.", 2);
        assertThat(top.getFirst().getElement()).isEqualTo("the");
        assertThat(top.getFirst().getCount()).isEqualTo(3);
        assertThat(top).hasSize(2);
    }
}
