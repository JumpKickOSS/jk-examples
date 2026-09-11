package com.example.petshop.db

import com.example.petshop.domain.Pet
import com.example.petshop.domain.PetRepository
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedPetRepository : PetRepository {
    override fun list(): List<Pet> = transaction { PetsTable.selectAll().map(::toPet) }

    override fun create(name: String, species: String): Pet = transaction {
        val id =
            PetsTable.insert {
                it[PetsTable.name] = name
                it[PetsTable.species] = species
            } get PetsTable.id
        Pet(id, name, species)
    }

    override fun bySpecies(species: String?): List<Pet> {
        if (species.isNullOrBlank()) return list()
        val key = species.trim().lowercase()
        return list().filter { it.species.equals(key, ignoreCase = true) }
    }

    private fun toPet(row: ResultRow) =
        Pet(id = row[PetsTable.id], name = row[PetsTable.name], species = row[PetsTable.species])
}
