package dev.jkexamples.vcli;

import org.apache.commons.lang3.StringUtils;

/** The prod product — uses the dependency only this variant declares. */
final class Backend {
    String describe() {
        return StringUtils.capitalize("prod (real backend via commons-lang3)");
    }
}
