package com.example.petshop.web;
import static org.junit.jupiter.api.Assertions.assertFalse;
import com.example.petshop.service.PetService;
import org.junit.jupiter.api.Test;
class PetServiceTest {
    @Test
    void seedsFido() {
        assertFalse(new PetService().list().isEmpty());
    }
}
