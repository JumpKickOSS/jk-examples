package com.example.petshop.web;
import com.example.petshop.domain.Pet;
import com.example.petshop.service.PetService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class PetController {
    private final PetService pets = new PetService();
    @GetMapping("/pets")
    public List<Pet> pets() { return pets.list(); }
}
