package com.example.petshop

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PetRoutesTest {

    @Test
    fun listsAndCreatesPets() = testApplication {
        application { module("jdbc:h2:mem:test_petshop_${System.nanoTime()};DB_CLOSE_DELAY=-1") }

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
