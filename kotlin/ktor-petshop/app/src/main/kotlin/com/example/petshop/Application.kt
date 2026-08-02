package com.example.petshop

import com.example.petshop.db.DatabaseFactory
import com.example.petshop.di.appModule
import com.example.petshop.routes.petRoutes
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    install(ContentNegotiation) {
        jackson()
    }
    val repo = get<PetRepository>()
    if (repo.list().isEmpty()) {
        repo.create("Fido", "dog")
        repo.create("Whiskers", "cat")
    }
    routing {
        petRoutes()
    }
}
