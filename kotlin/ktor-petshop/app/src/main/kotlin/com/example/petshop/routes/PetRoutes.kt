package com.example.petshop.routes

import com.example.petshop.domain.PetRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

data class NewPet(val name: String, val species: String)

fun Route.petRoutes() {
    val pets by inject<PetRepository>()

    route("/api/pets") {
        get {
            val species = call.request.queryParameters["species"]
            call.respond(pets.bySpecies(species))
        }
        post {
            val body = call.receive<NewPet>()
            val created = pets.create(body.name, body.species)
            call.respond(HttpStatusCode.Created, created)
        }
    }
}
