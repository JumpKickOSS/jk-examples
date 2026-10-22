package dev.jkexamples.links.cli;

import dev.jkexamples.links.LinkStore;
import dev.jkexamples.links.ShortLink;
import dev.jkexamples.links.store.FileLinkStore;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.time.Clock;

/** {@code links add <url> | open <code> | list}, stored in {@code ./links.tsv} (or {@code $LINKS_FILE}). */
public final class Main {

    private Main() {}

    public static void main(String[] args) throws Exception {
        String file = System.getenv().getOrDefault("LINKS_FILE", "links.tsv");
        System.exit(run(new FileLinkStore(Path.of(file), Clock.systemUTC()), args, System.out, System.err));
    }

    static int run(LinkStore store, String[] args, PrintStream out, PrintStream err) {
        if (args.length == 2 && args[0].equals("add")) {
            ShortLink link = store.add(URI.create(args[1]));
            out.println(link.code());
            return 0;
        }
        if (args.length == 2 && args[0].equals("open")) {
            var link = store.find(args[1]);
            if (link.isEmpty()) {
                err.println("no link " + args[1]);
                return 1;
            }
            out.println(link.get().target());
            return 0;
        }
        if (args.length == 1 && args[0].equals("list")) {
            store.all().forEach(l -> out.println(l.code() + "  " + l.target()));
            return 0;
        }
        err.println("usage: links add <url> | open <code> | list");
        return 2;
    }
}
