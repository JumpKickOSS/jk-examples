package dev.jkexamples.hotel.rest;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The API as a browser sees it: one cookie jar (the session and the XSRF-TOKEN cookie), the CSRF
 * header echoed on every write, JSON in and out.
 */
final class ApiClient {

    private final CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    private final HttpClient http =
            HttpClient.newBuilder().cookieHandler(cookies).build();
    private final String base;

    ApiClient(int port) {
        this.base = "http://localhost:" + port;
    }

    HttpResponse<String> get(String path) throws Exception {
        return http.send(
                HttpRequest.newBuilder(URI.create(base + path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> post(String path, String json) throws Exception {
        if (xsrf().isEmpty()) get("/api/hotels");
        HttpRequest request = HttpRequest.newBuilder(URI.create(base + path))
                .header("Content-Type", json.startsWith("{") ? "application/json" : "application/x-www-form-urlencoded")
                .header("X-XSRF-TOKEN", xsrf())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String xsrf() {
        return cookies.getCookieStore().getCookies().stream()
                .filter(c -> c.getName().equals("XSRF-TOKEN"))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElse("");
    }
}
