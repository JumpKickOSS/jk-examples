package com.example.petshop.domain

interface PetRepository {
    fun list(): List<Pet>

    fun create(name: String, species: String): Pet

    fun bySpecies(species: String?): List<Pet>
}
