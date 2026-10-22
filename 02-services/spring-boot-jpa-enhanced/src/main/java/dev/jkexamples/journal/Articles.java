package dev.jkexamples.journal;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Articles extends JpaRepository<Article, Long> {

    Optional<Article> findBySlug(String slug);

    List<Article> findAllByOrderByPublishedDesc();
}
