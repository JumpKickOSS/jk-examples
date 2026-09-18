package com.acme.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** The unit tier: both tools against an in-memory H2, no Docker. */
class MigrationsH2Test {

    @Test
    void bothToolsApplyToAnEmptyDatabase() throws Exception {
        String url = "jdbc:h2:mem:migrations-" + System.nanoTime() + ";DB_CLOSE_DELAY=-1";

        Migrations.Report report = Migrations.apply(url, "sa", "");

        assertEquals(List.of("1", "2"), report.flywayApplied());
        assertEquals(1, report.liquibaseChangeSets());
        assertTrue(report.tables().containsAll(List.of("customers", "orders", "audit_log")), report.toString());
    }

    @Test
    void aSecondRunAppliesNothing() throws Exception {
        String url = "jdbc:h2:mem:migrations-" + System.nanoTime() + ";DB_CLOSE_DELAY=-1";
        Migrations.apply(url, "sa", "");

        Migrations.Report again = Migrations.apply(url, "sa", "");

        assertEquals(List.of(), again.flywayApplied());
        assertEquals(1, again.liquibaseChangeSets());
    }
}
