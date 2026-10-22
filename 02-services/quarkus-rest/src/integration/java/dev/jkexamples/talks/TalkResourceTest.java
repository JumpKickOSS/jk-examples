package dev.jkexamples.talks;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

/** The API on PostgreSQL 18 with the real migrations; Dev Services starts the container. */
@QuarkusTest
class TalkResourceTest {

    @Test
    void the_schedule_is_seeded_and_filters_by_track() {
        given().get("/talks").then().statusCode(200).body("$", hasSize(4));
        given().queryParam("track", "jvm").get("/talks").then().statusCode(200).body("track", everyItem(is("jvm")));
    }

    @Test
    void a_talk_is_scheduled_and_cancelled() {
        String location = given().contentType("application/json")
                .body("""
                        {"title": "Supply chains", "speaker": "Elena Eriksen", "track": "build", "room": "Vine",
                         "startsAt": "2026-11-12T16:00:00", "minutes": 30}""")
                .post("/talks")
                .then().statusCode(201).extract().header("Location");
        given().delete(location).then().statusCode(204);
        given().get(location).then().statusCode(404);
    }

    @Test
    void a_talk_that_clashes_in_its_room_is_refused() {
        given().contentType("application/json")
                .body("""
                        {"title": "Overbooked", "speaker": "Femi Falade", "track": "jvm", "room": "Harbour",
                         "startsAt": "2026-11-12T09:45:00", "minutes": 30}""")
                .post("/talks")
                .then().statusCode(409);
    }

    @Test
    void an_invalid_talk_is_refused() {
        given().contentType("application/json").body("{\"title\": \"\", \"minutes\": 1}").post("/talks").then().statusCode(400);
    }

    @Test
    void health_is_up() {
        given().get("/q/health/ready").then().statusCode(200);
    }
}
