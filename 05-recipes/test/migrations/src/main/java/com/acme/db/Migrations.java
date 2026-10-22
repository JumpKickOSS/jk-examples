package com.acme.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.exception.LiquibaseException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

/** Applies the module's Flyway migrations and its Liquibase change log to one database. */
public final class Migrations {

    /** The Liquibase change log, a resource of the module. */
    public static final String CHANGELOG = "db/changelog/db.changelog-master.yaml";

    private Migrations() {}

    /** What one run did: the Flyway versions applied, the change sets Liquibase has run, the tables. */
    public record Report(List<String> flywayApplied, int liquibaseChangeSets, List<String> tables) {
        @Override
        public String toString() {
            return "flyway: " + flywayApplied.size() + " applied " + flywayApplied
                    + " · liquibase: " + liquibaseChangeSets + " change set(s) · tables: "
                    + String.join(", ", tables);
        }
    }

    /** Runs {@code flyway migrate} then {@code liquibase update} against {@code url}. */
    public static Report apply(String url, String user, String password) throws Exception {
        MigrateResult flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration")
                .load()
                .migrate();
        List<String> applied = flyway.migrations.stream().map(m -> m.version).toList();

        liquibase(url, user, password);

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return new Report(applied, changeSets(connection), tables(connection));
        }
    }

    private static void liquibase(String url, String user, String password) throws LiquibaseException {
        CommandScope update = new CommandScope(UpdateCommandStep.COMMAND_NAME);
        update.addArgumentValue(DbUrlConnectionArgumentsCommandStep.URL_ARG, url);
        update.addArgumentValue(DbUrlConnectionArgumentsCommandStep.USERNAME_ARG, user);
        update.addArgumentValue(DbUrlConnectionArgumentsCommandStep.PASSWORD_ARG, password);
        update.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, CHANGELOG);
        update.execute();
    }

    private static int changeSets(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement.executeQuery("select count(*) from databasechangelog")) {
            return rows.next() ? rows.getInt(1) : 0;
        }
    }

    /** The user tables of the connection's schema, lower-cased and sorted. */
    public static List<String> tables(Connection connection) throws SQLException {
        List<String> names = new ArrayList<>();
        try (ResultSet rows = connection
                .getMetaData()
                .getTables(null, connection.getSchema(), "%", new String[] {"TABLE", "BASE TABLE"})) {
            while (rows.next()) {
                names.add(rows.getString("TABLE_NAME").toLowerCase(Locale.ROOT));
            }
        }
        return names.stream().sorted().toList();
    }
}
