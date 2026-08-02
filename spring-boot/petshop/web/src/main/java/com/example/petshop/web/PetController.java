package com.example.petshop.web;

import com.example.petshop.domain.Pet;
import com.example.petshop.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    public record NewPet(
            @NotBlank @Size(max = 80) String name, @NotBlank @Size(max = 40) String species) {}

    private final PetService pets;

    public PetController(PetService pets) {
        this.pets = pets;
    }

    @GetMapping
    public List<Pet> list(@RequestParam(required = false) String species) {
        return pets.bySpecies(species);
    }

    @PostMapping
    public ResponseEntity<Pet> create(@Valid @RequestBody NewPet body) {
        Pet saved = pets.create(body.name(), body.species());
        return ResponseEntity.created(URI.create("/api/pets/" + saved.id())).body(saved);
    }
}
