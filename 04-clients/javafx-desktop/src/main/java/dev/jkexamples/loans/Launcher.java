package dev.jkexamples.loans;

import javafx.application.Application;

/**
 * The main class. It does not extend {@link Application}, so the fat jar starts with {@code java
 * -jar}: the JVM's launcher refuses a JavaFX {@code Application} main outside the module path.
 */
public final class Launcher {

    private Launcher() {}

    public static void main(String[] args) {
        Application.launch(LoanApp.class, args);
    }
}
