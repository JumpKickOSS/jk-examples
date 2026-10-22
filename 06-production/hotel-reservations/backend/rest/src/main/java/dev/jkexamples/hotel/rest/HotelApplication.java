package dev.jkexamples.hotel.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Kestrel & Vine Hotels. Scans the service module's beans beside this one's. */
@SpringBootApplication(scanBasePackages = "dev.jkexamples.hotel")
public class HotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }
}
