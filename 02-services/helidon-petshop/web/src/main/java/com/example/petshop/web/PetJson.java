package com.example.petshop.web;

import com.example.petshop.domain.Pet;
import io.helidon.common.Api;
import io.helidon.json.binding.Json;

/** A pet as the API writes it: the domain record stays free of JSON annotations. */
@SuppressWarnings(Api.SUPPRESS_PREVIEW) // Helidon JSON binding is a preview API in Helidon 27
@Json.Entity
public record PetJson(long id, String name, String species) {

    static PetJson of(Pet pet) {
        return new PetJson(pet.id(), pet.name(), pet.species());
    }
}
