package com.acme.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

/** The integration tier: the same two tools against a PostgreSQL Testcontainer. */
@Tag("integration")
class MigrationsPostgresTest {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18-alpine");

    @BeforeAll
    static void start() {
        POSTGRES.start();
    }

    @AfterAll
    static void stop() {
        POSTGRES.stop();
    }

    @Test
    void bothToolsMigrateARealPostgres() throws Exception {
        Migrations.Report report =
                Migrations.apply(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());

        assertEquals(List.of("1", "2"), report.flywayApplied());
        assertEquals(1, report.liquibaseChangeSets());
        assertEquals(
                List.of(
                        "audit_log",
                        "customers",
                        "databasechangelog",
                        "databasechangeloglock",
                        "flyway_schema_history",
                        "orders"),
                report.tables());

        Migrations.Report again =
                Migrations.apply(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        assertTrue(again.flywayApplied().isEmpty(), "a second migrate applies nothing");
        assertEquals(1, again.liquibaseChangeSets(), "a second update runs no change set");
    }
}
