package dev.jkexamples.wordfreq.core;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import java.util.Locale;

/** Word frequencies, most frequent first. */
public final class Counter {

    private static final Splitter WORDS = Splitter.onPattern("[^\\p{L}']+").omitEmptyStrings();

    private Counter() {}

    public static List<Multiset.Entry<String>> top(String text, int n) {
        ImmutableMultiset<String> words = ImmutableMultiset.copyOf(WORDS.split(text.toLowerCase(Locale.ROOT)));
        return Multisets.copyHighestCountFirst(words).entrySet().stream().limit(n).toList();
    }
}
