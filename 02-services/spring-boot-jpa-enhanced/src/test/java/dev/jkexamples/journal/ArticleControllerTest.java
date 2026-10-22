package dev.jkexamples.journal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** The app as `jk run` starts it, seeded, over MockMvc. */
@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void the_index_lists_newest_first_with_comment_counts() throws Exception {
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("enhancement"))
                .andExpect(jsonPath("$[1].comments").value(1));
    }

    @Test
    void an_article_page_has_its_body_and_comments() throws Exception {
        mvc.perform(get("/articles/lockfiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0]").value("ada: Reproducible at last."));
        mvc.perform(get("/articles/missing")).andExpect(status().isNotFound());
    }
}
