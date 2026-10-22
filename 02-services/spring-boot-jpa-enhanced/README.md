# 02-services/spring-boot-jpa-enhanced — JPA entities enhanced at build time

`journal` is a Spring Boot 4 API over two JPA entities, `Article` and `Comment`. Hibernate's
build-time bytecode enhancement changes how they behave, and `[hibernate]` turns it on:

```toml
[hibernate]
enhance                = true
lazy-initialization    = true    # @Basic(fetch = LAZY) is honoured: the article body loads when read
dirty-tracking         = true    # the entity records its own changes; flush skips the diff
association-management = true    # comment.setArticle(a) also adds the comment to a.getComments()
```

The `hibernate-enhance` step runs the enhancer of the project's own `hibernate-core` (the version
Boot's BOM picks) over `target/classes` after compile. From then on the enhanced classes are
the module's classes, used by `jk test`, `jk run` and the boot jar alike. There is no Maven or Gradle
plugin to keep in step with the Hibernate version.

```sh
jk build                     # compile → enhance → tests (6, H2 in memory)
jk run                       # seeded with two articles
curl localhost:8080/articles             # the index: titles and comment counts, no bodies loaded
curl localhost:8080/articles/lockfiles   # one article: its body and comments
```

## What the tests prove

`EnhancementTest` (`@DataJpaTest`) checks each switch through Hibernate itself:

| Test | Without enhancement |
|---|---|
| `the_body_loads_only_when_read`: `Hibernate.isPropertyInitialized(a, "body")` stays false until `getBody()` | The body loads with every `find`. |
| `a_change_is_tracked_by_the_entity_itself`: `$$_hibernate_getDirtyAttributes()` is `[title]` | No such method; Hibernate diffs the entity at flush. |
| `setting_a_comments_article_adds_it_to_the_article` | `a.getComments()` stays empty until reloaded. |

Set `enhance = false` and all six tests fail. That includes the controller tests, because the seed
relies on association management to save its comment.

## Teaches

- Build-time enhancement in one table, applied to the classes every later step uses.
- Why it matters: lazy basic attributes, cheap flushes, consistent in-memory associations.

## Validates

- The hibernate plugin: the enhancer from the resolved `hibernate-core`, each switch, the cached
  step, and enhanced classes reaching `jk test` and the jar.
