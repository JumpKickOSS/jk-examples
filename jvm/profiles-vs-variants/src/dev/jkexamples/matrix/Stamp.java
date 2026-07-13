package dev.jkexamples.matrix;

/**
 * Reports the build type by looking for ReleaseStamp, which only the release variant's
 * extra-src compiles in. extra-src is additive (never an override) — a variant that needs
 * different behavior adds a class, it doesn't shadow one.
 */
final class Stamp {
    static String describe() {
        try {
            return String.valueOf(Class.forName("dev.jkexamples.matrix.ReleaseStamp")
                    .getField("STAMP")
                    .get(null));
        } catch (ReflectiveOperationException e) {
            return "debug build";
        }
    }
}
