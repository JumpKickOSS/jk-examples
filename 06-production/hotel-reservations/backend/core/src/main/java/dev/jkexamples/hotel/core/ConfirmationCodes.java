package dev.jkexamples.hotel.core;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

/**
 * Confirmation codes a guest reads over the phone: {@code KV-} and six characters from an alphabet
 * with no {@code 0/O} or {@code 1/I/L} to confuse.
 */
public final class ConfirmationCodes {

    static final String ALPHABET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    public static final String PREFIX = "KV-";
    private static final int LENGTH = 6;

    private final RandomGenerator random;

    public ConfirmationCodes() {
        this(new SecureRandom());
    }

    public ConfirmationCodes(RandomGenerator random) {
        this.random = random;
    }

    public String next() {
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < LENGTH; i++) sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    public static boolean isWellFormed(String code) {
        if (!code.startsWith(PREFIX) || code.length() != PREFIX.length() + LENGTH) return false;
        return code.substring(PREFIX.length()).chars().allMatch(c -> ALPHABET.indexOf(c) >= 0);
    }
}
