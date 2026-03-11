package com.telco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class TelcoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelcoApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to Telco appV1.0  DevOps Project íº€";
    }

    @GetMapping("/health")
    public String health() {
        return "Application is healthy";
    }
}
