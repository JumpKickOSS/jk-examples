package com.example.petshop

import com.example.petshop.db.DatabaseFactory
import com.example.petshop.di.appModule
import com.example.petshop.domain.PetRepository
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
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") { module() }.start(wait = true)
}

/** The whole app: the database, Koin, JSON and the routes, seeded with two pets on first start. */
fun Application.module(jdbcUrl: String = DatabaseFactory.DEFAULT_URL) {
    DatabaseFactory.init(jdbcUrl)
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    install(ContentNegotiation) { jackson() }
    val repo = get<PetRepository>()
    if (repo.list().isEmpty()) {
        repo.create("Fido", "dog")
        repo.create("Whiskers", "cat")
    }
    routing { petRoutes() }
}
