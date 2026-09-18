package com.acme.db;

/**
 * Migrates the database named by {@code JDBC_URL} (with {@code DB_USER} and {@code DB_PASSWORD}),
 * an H2 file under target/ when nothing is set, and prints what the run did.
 */
public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : env("JDBC_URL", "jdbc:h2:file:./target/dev");
        String user = env("DB_USER", "sa");
        String password = env("DB_PASSWORD", "");
        System.out.println(Migrations.apply(url, user, password));
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
