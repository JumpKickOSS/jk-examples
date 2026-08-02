package com.example.petshop.service;
import com.example.petshop.domain.Pet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
public final class PetService {
    private final AtomicLong ids = new AtomicLong();
    private final List<Pet> pets = new CopyOnWriteArrayList<>();
    public PetService() { pets.add(new Pet(ids.incrementAndGet(), "Fido", "dog")); }
    public List<Pet> list() { return List.copyOf(pets); }
    public Pet add(String name, String species) {
        Pet p = new Pet(ids.incrementAndGet(), name, species);
        pets.add(p);
        return p;
    }
}
