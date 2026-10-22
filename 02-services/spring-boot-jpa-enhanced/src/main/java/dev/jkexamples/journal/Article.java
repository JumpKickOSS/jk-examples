package dev.jkexamples.journal;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A journal article. The body is the large column: it loads only when read, which Hibernate does
 * for a basic attribute only when the class is enhanced.
 */
@Entity
public class Article {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    private LocalDate published;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String body;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    protected Article() {}

    public Article(String slug, String title, LocalDate published, String body) {
        this.slug = slug;
        this.title = title;
        this.published = published;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getPublished() {
        return published;
    }

    public String getBody() {
        return body;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
