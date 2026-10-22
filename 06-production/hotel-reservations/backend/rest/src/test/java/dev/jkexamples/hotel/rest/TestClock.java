package dev.jkexamples.hotel.rest;

import java.time.Clock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/** The clock a web slice needs without the service module's configuration. */
@TestConfiguration(proxyBeanMethods = false)
class TestClock {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
