package com.example.petshop.domain;

/** A pet in the shop: plain data, with no Helidon in it. */
public record Pet(long id, String name, String species) {}
