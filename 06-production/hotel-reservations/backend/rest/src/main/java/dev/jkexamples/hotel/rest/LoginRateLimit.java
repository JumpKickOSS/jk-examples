package dev.jkexamples.hotel.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * At most {@value #ATTEMPTS} sign-in attempts per client address per minute; the next gets 429 with
 * a {@code Retry-After}. In memory, so per instance: enough to make password guessing slow, not a
 * substitute for a gateway's limits.
 */
@Component
class LoginRateLimit extends OncePerRequestFilter {

    static final int ATTEMPTS = 10;
    static final Duration WINDOW = Duration.ofMinutes(1);

    private final ConcurrentHashMap<String, Deque<Long>> attempts = new ConcurrentHashMap<>();
    private final Clock clock;

    LoginRateLimit(Clock clock) {
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) && "/api/auth/login".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long now = clock.millis();
        Deque<Long> times = attempts.computeIfAbsent(request.getRemoteAddr(), k -> new ArrayDeque<>());
        synchronized (times) {
            while (!times.isEmpty() && times.peekFirst() < now - WINDOW.toMillis()) times.pollFirst();
            if (times.size() >= ATTEMPTS) {
                response.setHeader("Retry-After", Long.toString(WINDOW.toSeconds()));
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
                return;
            }
            times.addLast(now);
        }
        chain.doFilter(request, response);
    }
}
