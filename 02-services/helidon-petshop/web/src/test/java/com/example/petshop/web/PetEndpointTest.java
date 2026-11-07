package com.example.petshop.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import io.helidon.common.GenericType;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.HeaderNames;
import io.helidon.http.Status;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientRequest;
import io.helidon.webclient.http1.Http1ClientResponse;
import io.helidon.webserver.testing.junit5.ServerTest;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The API on a server of its own, on a free port, with the registry's real services behind it. */
@ServerTest
class PetEndpointTest {

    private static final GenericType<List<PetJson>> PETS = new GenericType<>() {};

    private final Http1Client client;

    PetEndpointTest(Http1Client client) {
        this.client = client;
    }

    @Test
    void the_shop_opens_with_fido_and_whiskers() {
        assertThat(names(client.get("/api/pets")), is(List.of("Fido", "Whiskers")));
    }

    @Test
    void species_filters_ignoring_case() {
        assertThat(names(client.get("/api/pets").queryParam("species", "CAT")), is(List.of("Whiskers")));
    }

    @Test
    void a_post_adds_a_pet_and_says_where_it_is() {
        try (Http1ClientResponse response = client.post("/api/pets")
                .contentType(MediaTypes.APPLICATION_JSON)
                .submit("{\"name\":\"Rex\",\"species\":\"dog\"}")) {
            assertThat(response.status(), is(Status.CREATED_201));
            String location = response.headers().get(HeaderNames.LOCATION).get();
            assertThat(location, startsWith("/api/pets/"));
            PetJson rex = client.get(location).requestEntity(PetJson.class);
            assertThat(rex.name(), is("Rex"));
            assertThat(rex.species(), is("dog"));
        }
    }

    @Test
    void a_blank_name_is_refused() {
        try (Http1ClientResponse response = client.post("/api/pets")
                .contentType(MediaTypes.APPLICATION_JSON)
                .submit("{\"name\":\" \",\"species\":\"dog\"}")) {
            assertThat(response.status(), is(Status.BAD_REQUEST_400));
        }
    }

    @Test
    void an_unknown_pet_is_not_found() {
        try (Http1ClientResponse response = client.get("/api/pets/999999").request()) {
            assertThat(response.status(), is(Status.NOT_FOUND_404));
        }
    }

    @Test
    void the_database_is_ready() {
        try (Http1ClientResponse response = client.get("/observe/health/ready").request()) {
            assertThat(response.status(), is(Status.NO_CONTENT_204));
        }
    }

    private static List<String> names(Http1ClientRequest request) {
        return request.requestEntity(PETS).stream().map(PetJson::name).toList();
    }
}
