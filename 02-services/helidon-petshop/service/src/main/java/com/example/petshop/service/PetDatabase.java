package com.example.petshop.service;

import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.service.registry.Service;
import java.util.function.Supplier;

/**
 * The application's one {@link DbClient}, made from the {@code db} section of the configuration:
 * a supplier service, so whatever injects a {@code DbClient} gets this one.
 */
@Service.Singleton
class PetDatabase implements Supplier<DbClient> {

    private final DbClient client;

    @Service.Inject
    PetDatabase(Config config) {
        this.client = DbClient.create(config.get("db"));
    }

    @Override
    public DbClient get() {
        return client;
    }
}
