package dev.jkexamples.hotel.service.internal;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** One PostgreSQL 18 container for the tier, migrated and seeded exactly as production is. */
final class PostgresSchema {

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18-alpine");

    private static @Nullable DSLContext db;

    private PostgresSchema() {}

    static synchronized DSLContext db() {
        DSLContext db = PostgresSchema.db;
        if (db == null) {
            POSTGRES.start();
            DataSource ds = new DriverManagerDataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
            migrate(ds);
            db = DSL.using(ds, SQLDialect.POSTGRES);
            PostgresSchema.db = db;
        }
        return db;
    }

    private static void migrate(DataSource ds) {
        Flyway.configure().dataSource(ds).locations("classpath:db/migration", "classpath:db/seed").load().migrate();
    }
}
