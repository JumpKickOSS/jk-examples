package dev.jkexamples.journal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/** A reader's comment. Setting its article adds it to that article's comments: association management. */
@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Article article;

    private String author;
    private String text;

    protected Comment() {}

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
