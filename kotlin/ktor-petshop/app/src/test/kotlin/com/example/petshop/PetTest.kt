package com.example.petshop
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
class PetTest {
    @Test fun name() { assertEquals("Fido", Pet(1, "Fido", "dog").name) }
}
