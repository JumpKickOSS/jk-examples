package com.example.petshop;

import com.example.petshop.service.PetService;
import com.example.petshop.web.PetController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.petshop")
@Import({PetService.class, PetController.class})
public class PetshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetshopApplication.class, args);
    }
}
