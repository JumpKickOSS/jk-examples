package dev.jkexamples.calc;

/** A program that parses but cannot be evaluated, or does not parse. */
public final class CalcException extends RuntimeException {

    public CalcException(String message) {
        super(message);
    }
}
