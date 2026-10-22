package dev.jkexamples.journal;

import java.time.LocalDate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Two articles for `jk run`; tests bring their own. */
@Configuration
@Profile("!test")
class Seed {

    @Bean
    ApplicationRunner seedArticles(Articles articles) {
        return args -> {
            if (articles.count() > 0) return;
            Article lock = new Article("lockfiles", "Lockfiles are law", LocalDate.of(2026, 9, 1), "A lockfile is a promise. ".repeat(400));
            Comment c = new Comment("ada", "Reproducible at last.");
            c.setArticle(lock);
            articles.save(lock);
            articles.save(new Article("enhancement", "What bytecode enhancement buys", LocalDate.of(2026, 10, 1), "Lazy basics, dirty tracking. ".repeat(400)));
        };
    }
}
