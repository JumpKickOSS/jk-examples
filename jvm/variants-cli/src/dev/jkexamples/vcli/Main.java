package dev.jkexamples.vcli;

/** Prints which product this binary is — the Backend class comes from the selected variant. */
public final class Main {
    public static void main(String[] args) {
        System.out.println("variants-cli: " + new Backend().describe());
    }
}
