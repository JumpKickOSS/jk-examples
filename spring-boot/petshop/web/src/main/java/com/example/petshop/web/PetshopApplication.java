package com.example.petshop.web;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = "com.example.petshop")
public class PetshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetshopApplication.class, args);
    }
}
