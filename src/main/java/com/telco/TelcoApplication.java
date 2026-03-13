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
        return "Welcome to Telco App V1.4 - Login Feature 🚀";
    }

    @GetMapping("/login")
    public String login() {
        return "Login Feature is now available in V1.2";
    }
}