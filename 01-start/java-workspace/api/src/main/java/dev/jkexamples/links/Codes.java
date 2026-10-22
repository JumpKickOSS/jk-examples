package dev.jkexamples.links;

/** Base62 short codes: a sequence number written in {@code 0-9a-zA-Z}. */
public final class Codes {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private Codes() {}

    public static String encode(long sequence) {
        if (sequence < 0) throw new IllegalArgumentException("negative sequence");
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(ALPHABET.charAt((int) (sequence % 62)));
            sequence /= 62;
        } while (sequence > 0);
        return sb.reverse().toString();
    }

    public static long decode(String code) {
        long value = 0;
        for (char c : code.toCharArray()) {
            int digit = ALPHABET.indexOf(c);
            if (digit < 0) throw new IllegalArgumentException("not a short code: " + code);
            value = Math.multiplyExact(value, 62) + digit;
        }
        return value;
    }

    public static boolean isValid(String code) {
        return !code.isEmpty() && code.length() <= 11 && code.chars().allMatch(c -> ALPHABET.indexOf(c) >= 0);
    }
}
