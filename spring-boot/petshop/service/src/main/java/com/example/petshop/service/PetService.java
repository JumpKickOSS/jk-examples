package com.example.petshop.service;

import com.example.petshop.domain.Pet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    private final AtomicLong ids = new AtomicLong(1);
    private final List<Pet> pets = new CopyOnWriteArrayList<>();

    public PetService() {
        pets.add(new Pet(ids.getAndIncrement(), "Fido", "dog"));
        pets.add(new Pet(ids.getAndIncrement(), "Whiskers", "cat"));
    }

    public Pet create(String name, String species) {
        Pet p = new Pet(ids.getAndIncrement(), name, species);
        pets.add(p);
        return p;
    }

    public List<Pet> list() {
        return new ArrayList<>(pets);
    }

    public List<Pet> bySpecies(String species) {
        if (species == null || species.isBlank()) return list();
        String key = species.strip();
        return pets.stream().filter(p -> p.species().equalsIgnoreCase(key)).toList();
    }
}
