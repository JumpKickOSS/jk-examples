package com.example.petshop;

import io.helidon.logging.common.LogConfig;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.ServiceRegistryManager;
import io.helidon.service.registry.Services;
import io.helidon.webserver.WebServer;

/**
 * Starts the service registry, which starts the web server and every endpoint in it.
 *
 * <p>{@code @Service.GenerateBinding} has the build write {@code ApplicationBinding}: every service
 * of the workspace (the endpoint here, PetService and PetDatabase in the service module), bound at
 * build time instead of discovered at startup.
 */
@Service.GenerateBinding
public final class Main {

    static {
        LogConfig.initClass();
    }

    private Main() {}

    public static void main(String[] args) {
        LogConfig.configureRuntime();
        ServiceRegistryManager.start(ApplicationBinding.create());
        WebServer server = Services.get(WebServer.class);
        System.out.println("Pet shop is open: http://localhost:" + server.port() + "/api/pets");
    }
}
