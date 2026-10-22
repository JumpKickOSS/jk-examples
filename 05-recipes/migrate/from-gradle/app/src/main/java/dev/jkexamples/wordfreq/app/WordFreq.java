package dev.jkexamples.wordfreq.app;

import dev.jkexamples.wordfreq.core.Counter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "wordfreq", mixinStandardHelpOptions = true, description = "The most frequent words in a file.")
public final class WordFreq implements Callable<Integer> {

    @Option(names = {"-n", "--top"}, defaultValue = "10", description = "How many words")
    int top;

    @Parameters(paramLabel = "FILE")
    Path file;

    public static void main(String[] args) {
        System.exit(new CommandLine(new WordFreq()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        Counter.top(Files.readString(file), top).forEach(e -> System.out.println(e.getCount() + "\t" + e.getElement()));
        return 0;
    }
}
