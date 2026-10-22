package com.example.webapp.spa;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Client-side routing: a GET for a page the server does not know — no {@code /api} prefix, no
 * file extension, HTML acceptable — gets {@code index.html} from the web module's jar, never
 * cached, so {@code /about} works as a deep link and as a reload. Everything else that misses
 * stays the 404 problem details the rest of the API produces: an unknown {@code /api} route, a
 * missing {@code /assets/x.js}, a client that asked for JSON. The bundle's hashed assets are
 * served by Spring Boot's static resource handling and never reach this class.
 */
@ControllerAdvice
class SpaFallback {

    static final String INDEX = "static/index.html";

    private final byte[] index = load();

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<?> notFound(NoResourceFoundException e, HttpServletRequest request) {
        if (index != null && isPage(request)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .cacheControl(CacheControl.noStore())
                    .body(index);
        }
        return ResponseEntity.of(e.getBody()).build();
    }

    /** A page: GET or HEAD, outside the API, no extension in the last segment, and the client can take HTML. */
    static boolean isPage(HttpServletRequest request) {
        String method = request.getMethod();
        if (!"GET".equals(method) && !"HEAD".equals(method)) return false;
        String path = request.getRequestURI();
        if (path.startsWith("/api/") || path.equals("/api")) return false;
        String last = path.substring(path.lastIndexOf('/') + 1);
        if (last.contains(".")) return false;
        Optional<String> accept = Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT));
        return accept.isEmpty()
                || accept.get().contains("text/html")
                || accept.get().contains("*/*");
    }

    private static byte[] load() {
        try (InputStream in = SpaFallback.class.getClassLoader().getResourceAsStream(INDEX)) {
            return in == null ? null : in.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("cannot read " + INDEX, e);
        }
    }
}
