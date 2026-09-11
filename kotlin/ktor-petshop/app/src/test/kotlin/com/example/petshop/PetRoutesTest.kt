package com.example.petshop

import com.example.petshop.db.DatabaseFactory
import com.example.petshop.di.appModule
import com.example.petshop.domain.PetRepository
import com.example.petshop.routes.petRoutes
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

class PetRoutesTest {

    @Test
    fun listsAndCreatesPets() = testApplication {
        application {
            DatabaseFactory.init("jdbc:h2:mem:test_petshop_${System.nanoTime()};DB_CLOSE_DELAY=-1")
            install(Koin) { modules(appModule) }
            install(ContentNegotiation) { jackson() }
            val repo = get<PetRepository>()
            if (repo.list().isEmpty()) {
                repo.create("Fido", "dog")
            }
            routing { petRoutes() }
        }
        val list = client.get("/api/pets")
        assertEquals(HttpStatusCode.OK, list.status)
        assertTrue(list.bodyAsText().contains("Fido"))

        val created =
            client.post("/api/pets") {
                contentType(ContentType.Application.Json)
                setBody("""{"name":"Rex","species":"dog"}""")
            }
        assertEquals(HttpStatusCode.Created, created.status)
        assertTrue(created.bodyAsText().contains("Rex"))
    }
}
