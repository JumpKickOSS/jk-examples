package dev.jkexamples.journal;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/** What the enhancer changed, observed through Hibernate on an in-memory database. */
@DataJpaTest
@ActiveProfiles("test")
class EnhancementTest {

    @Autowired
    EntityManager em;

    @Autowired
    Articles articles;

    private long id;

    @BeforeEach
    void anArticle() {
        Article a = new Article("enhanced", "Enhanced", LocalDate.of(2026, 10, 22), "x".repeat(10_000));
        em.persist(a);
        em.flush();
        em.clear();
        id = a.getId();
    }

    @Test
    void the_entities_are_enhanced() {
        assertThat(ManagedEntity.class).isAssignableFrom(Article.class);
        assertThat(SelfDirtinessTracker.class).isAssignableFrom(Comment.class);
    }

    @Test
    void the_body_loads_only_when_read() {
        Article a = em.find(Article.class, id);
        assertThat(Hibernate.isPropertyInitialized(a, "body")).isFalse();
        assertThat(a.getBody()).hasSize(10_000);
        assertThat(Hibernate.isPropertyInitialized(a, "body")).isTrue();
    }

    @Test
    void a_change_is_tracked_by_the_entity_itself() {
        Article a = em.find(Article.class, id);
        a.setTitle("Retitled");
        assertThat(((SelfDirtinessTracker) a).$$_hibernate_getDirtyAttributes()).containsExactly("title");
    }

    @Test
    void setting_a_comments_article_adds_it_to_the_article() {
        Article a = em.find(Article.class, id);
        Comment c = new Comment("ada", "Nice.");
        c.setArticle(a);
        assertThat(a.getComments()).containsExactly(c);
    }
}
