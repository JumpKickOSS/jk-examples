package com.example.webapp.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    public record Hello(String message) {}

    @GetMapping("/hello")
    public Hello hello() {
        return new Hello("hello from the JVM");
    }
}
