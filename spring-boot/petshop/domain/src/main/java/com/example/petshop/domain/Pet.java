package com.example.petshop.domain;

/** Pure domain model (no Spring). */
public record Pet(Long id, String name, String species) {
    public Pet(String name, String species) {
        this(null, name, species);
    }
}
