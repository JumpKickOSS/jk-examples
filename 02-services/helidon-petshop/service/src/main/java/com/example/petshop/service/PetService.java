package com.example.petshop.service;

import com.example.petshop.domain.Pet;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbExecute;
import io.helidon.dbclient.DbResultDml;
import io.helidon.dbclient.DbRow;
import io.helidon.service.registry.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The pets, in the {@code pets} table: every statement is named in the configuration
 * ({@code db.statements}), so the SQL lives beside the connection it runs on.
 */
@Service.Singleton
public class PetService {

    private final DbClient db;

    @Service.Inject
    PetService(DbClient db) {
        this.db = db;
    }

    /** Creates the table and the two pets every shop opens with. */
    @Service.PostConstruct
    void open() {
        DbExecute exec = db.execute();
        exec.namedDml("create-table");
        if (exec.namedGet("count-pets").map(row -> row.column(1).getLong()).orElse(0L) == 0L) {
            create("Fido", "dog");
            create("Whiskers", "cat");
        }
    }

    public List<Pet> list() {
        return db.execute().namedQuery("select-all").map(PetService::pet).toList();
    }

    /** The pets of one species, ignoring case; every pet when {@code species} is blank. */
    public List<Pet> bySpecies(String species) {
        if (species.isBlank()) return list();
        return db.execute()
                .namedQuery("select-by-species", species.strip())
                .map(PetService::pet)
                .toList();
    }

    public Optional<Pet> find(long id) {
        return db.execute().namedGet("select-by-id", id).map(PetService::pet);
    }

    public Pet create(String name, String species) {
        DbResultDml result = db.execute()
                .createNamedInsert("insert")
                .params(name, species)
                .returnGeneratedKeys()
                .insert();
        try (Stream<DbRow> keys = result.generatedKeys()) {
            long id = keys.findFirst().orElseThrow().column(1).getLong();
            return new Pet(id, name, species);
        }
    }

    private static Pet pet(DbRow row) {
        return new Pet(
                row.column("id").getLong(),
                row.column("name").getString(),
                row.column("species").getString());
    }
}
