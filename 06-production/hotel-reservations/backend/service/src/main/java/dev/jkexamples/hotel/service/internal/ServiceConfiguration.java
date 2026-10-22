package dev.jkexamples.hotel.service.internal;

import dev.jkexamples.hotel.core.ConfirmationCodes;
import dev.jkexamples.hotel.core.PricingPolicy;
import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** The domain objects the services share. A test replaces the clock to pin "today". */
@Configuration(proxyBeanMethods = false)
class ServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    PricingPolicy pricingPolicy() {
        return new PricingPolicy();
    }

    @Bean
    ConfirmationCodes confirmationCodes() {
        return new ConfirmationCodes();
    }
}
