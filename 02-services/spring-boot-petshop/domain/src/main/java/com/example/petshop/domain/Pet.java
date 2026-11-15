package com.example.petshop.domain;

import org.jspecify.annotations.Nullable;

/** Pure domain model (no Spring). The id is null until the pet is stored. */
public record Pet(@Nullable Long id, String name, String species) {
    public Pet(String name, String species) {
        this(null, name, species);
    }
}
