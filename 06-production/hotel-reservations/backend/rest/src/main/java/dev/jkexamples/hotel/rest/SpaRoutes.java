package dev.jkexamples.hotel.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The Angular router owns every path that is not an API, an actuator endpoint or a file: those all
 * serve {@code index.html}, and the client-side router takes it from there.
 */
@Controller
class SpaRoutes {

    @GetMapping({"/", "/hotels/**", "/book/**", "/reservation/**", "/desk/**", "/search/**"})
    String index() {
        return "forward:/index.html";
    }
}
