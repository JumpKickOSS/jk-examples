package dev.jkexamples.hotel.service.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Migrations run at startup unless {@code hotel.migrate-on-start} is false. A runtime switch, not
 * {@code spring.flyway.enabled}: Spring AOT fixes property conditions at build time, so only a
 * value read while the app runs can turn migrations off for the image's AOT training run, which has
 * no database.
 */
@Configuration(proxyBeanMethods = false)
class MigrationConfiguration {

    @Bean
    FlywayMigrationStrategy migrateOnStart(@Value("${hotel.migrate-on-start:true}") boolean migrate) {
        return flyway -> {
            if (migrate) flyway.migrate();
        };
    }
}
