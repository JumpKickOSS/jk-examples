package com.example.petshop.web;

import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.health.DbClientHealthCheck;
import io.helidon.health.HealthCheck;
import io.helidon.health.HealthCheckResponse;
import io.helidon.health.HealthCheckType;
import io.helidon.service.registry.Service;

/** A readiness check in /observe/health: the database answers the db.health-check statement. */
@Service.Singleton
class DatabaseHealth implements HealthCheck {

    private final HealthCheck check;

    @Service.Inject
    DatabaseHealth(DbClient db, Config config) {
        this.check = DbClientHealthCheck.create(db, config.get("db.health-check"));
    }

    @Override
    public HealthCheckType type() {
        return HealthCheckType.READINESS;
    }

    @Override
    public String name() {
        return "database";
    }

    @Override
    public HealthCheckResponse call() {
        return check.call();
    }
}
