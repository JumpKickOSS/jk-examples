package dev.jkexamples.sums;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * {@code sums [-a ALGORITHM] FILE…} prints a checksum line per file, as {@code sha256sum} does;
 * {@code sums -c LIST} checks the files a list names. Exit 1 when a check fails.
 */
@Command(name = "sums", mixinStandardHelpOptions = true, version = "sums 0.1.0",
        description = "Print or check file checksums.")
public final class Sums implements Callable<Integer> {

    @Option(names = {"-a", "--algorithm"}, defaultValue = "SHA-256", description = "SHA-256 (default), SHA-512, SHA-1, MD5")
    String algorithm;

    @Option(names = {"-c", "--check"}, description = "Read checksum lines from FILE and verify them")
    Path check;

    @Parameters(arity = "0..*", paramLabel = "FILE")
    List<Path> files = List.of();

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public static void main(String[] args) {
        System.exit(new CommandLine(new Sums()).execute(args));
    }

    @Override
    public Integer call() throws IOException, NoSuchAlgorithmException {
        PrintWriter out = spec.commandLine().getOut();
        if (check != null) return verify(out);
        if (files.isEmpty()) throw new CommandLine.ParameterException(spec.commandLine(), "name at least one FILE, or -c LIST");
        for (Path f : files) out.println(digest(f) + "  " + f);
        out.flush();
        return 0;
    }

    private int verify(PrintWriter out) throws IOException, NoSuchAlgorithmException {
        int failed = 0;
        for (String line : Files.readAllLines(check, StandardCharsets.UTF_8)) {
            if (line.isBlank()) continue;
            int gap = line.indexOf("  ");
            if (gap < 0) throw new CommandLine.ParameterException(spec.commandLine(), "not a checksum line: " + line);
            Path file = Path.of(line.substring(gap + 2));
            boolean ok = Files.exists(file) && digest(file).equals(line.substring(0, gap));
            out.println(file + ": " + (ok ? "OK" : "FAILED"));
            if (!ok) failed++;
        }
        out.flush();
        return failed == 0 ? 0 : 1;
    }

    String digest(Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buf = new byte[64 * 1024];
            for (int n; (n = in.read(buf)) > 0; ) md.update(buf, 0, n);
        }
        return HexFormat.of().formatHex(md.digest());
    }
}
