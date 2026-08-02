package com.example.petshop
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
fun main() {
    val pets = listOf(Pet(1, "Fido", "dog"))
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { jackson() }
        routing {
            get("/pets") { call.respond(pets) }
        }
    }.start(wait = true)
}
