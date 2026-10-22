package dev.jkexamples.journal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** The index never reads a body, so its query never loads one; an article page reads it. */
@RestController
@RequestMapping("/articles")
@Transactional(readOnly = true)
public class ArticleController {

    public record Summary(String slug, String title, LocalDate published, int comments) {}

    public record Page(String slug, String title, LocalDate published, String body, List<String> comments) {}

    private final Articles articles;

    public ArticleController(Articles articles) {
        this.articles = articles;
    }

    @GetMapping
    public List<Summary> index() {
        return articles.findAllByOrderByPublishedDesc().stream()
                .map(a -> new Summary(a.getSlug(), a.getTitle(), a.getPublished(), a.getComments().size()))
                .toList();
    }

    @GetMapping("/{slug}")
    public Page article(@PathVariable String slug) {
        Article a = articles.findBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new Page(
                a.getSlug(),
                a.getTitle(),
                a.getPublished(),
                a.getBody(),
                a.getComments().stream().map(c -> c.getAuthor() + ": " + c.getText()).toList());
    }
}
