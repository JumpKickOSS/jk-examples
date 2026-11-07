package com.example.petshop.web;

import com.example.petshop.domain.Pet;
import com.example.petshop.service.PetService;
import io.helidon.common.Api;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.HeaderNames;
import io.helidon.http.Http;
import io.helidon.http.NotFoundException;
import io.helidon.http.Status;
import io.helidon.metrics.api.Metrics;
import io.helidon.service.registry.Service;
import io.helidon.validation.Validation;
import io.helidon.webserver.http.RestServer;
import io.helidon.webserver.http.ServerResponse;
import java.util.List;
import java.util.Optional;

/** {@code /api/pets}: list (optionally by species), fetch one, add one. */
@SuppressWarnings(Api.SUPPRESS_PREVIEW) // Helidon Declarative is a preview feature in Helidon 27
@RestServer.Endpoint
@Http.Path("/api/pets")
@Service.Singleton
class PetEndpoint {

    private final PetService pets;

    @Service.Inject
    PetEndpoint(PetService pets) {
        this.pets = pets;
    }

    @Http.GET
    @Http.Produces(MediaTypes.APPLICATION_JSON_VALUE)
    @Metrics.Counted
    List<PetJson> list(@Http.QueryParam("species") Optional<String> species) {
        return pets.bySpecies(species.orElse("")).stream().map(PetJson::of).toList();
    }

    @Http.GET
    @Http.Path("/{id}")
    @Http.Produces(MediaTypes.APPLICATION_JSON_VALUE)
    PetJson get(@Http.PathParam("id") long id) {
        return pets.find(id).map(PetJson::of).orElseThrow(() -> new NotFoundException("No pet " + id));
    }

    @Http.POST
    @Http.Consumes(MediaTypes.APPLICATION_JSON_VALUE)
    @Http.Produces(MediaTypes.APPLICATION_JSON_VALUE)
    @RestServer.Status(Status.CREATED_201_CODE)
    PetJson create(@Validation.Valid @Http.Entity NewPet body, ServerResponse response) {
        Pet saved = pets.create(body.name(), body.species());
        response.header(HeaderNames.LOCATION, "/api/pets/" + saved.id());
        return PetJson.of(saved);
    }
}
