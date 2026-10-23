package com.acme;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import org.junit.jupiter.api.Test;

class GreetingControllerTest {

    @Test
    void greets_by_name() {
        var body = Objects.requireNonNull(new GreetingController().getGreeting("Ada").getBody());
        assertThat(body.getMessage()).isEqualTo("Hello, Ada!");
    }
}
