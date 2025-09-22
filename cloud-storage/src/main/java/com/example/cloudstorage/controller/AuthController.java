package com.example.cloudstorage.controller;

import com.example.cloudstorage.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        System.out.println("Received credentials: " + credentials); // Логирование
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (username == null) {
            username = credentials.get("login"); // Поддержка ключа login
        }
        if (username == null || password == null) {
            System.out.println("Missing username or password");
            return ResponseEntity.ok(Map.of("auth-token", ""));
        }
        try {
            String token = authService.login(username, password);
            return ResponseEntity.ok(Map.of("auth-token", token));
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.ok(Map.of("auth-token", ""));
        }
    }
}