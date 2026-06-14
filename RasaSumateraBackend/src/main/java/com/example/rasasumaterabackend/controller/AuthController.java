package com.example.rasasumaterabackend.controller;

import com.example.rasasumaterabackend.dto.LoginRequest;
import com.example.rasasumaterabackend.model.User;
import com.example.rasasumaterabackend.repository.UserRepository;
import com.example.rasasumaterabackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Endpoint register (POST http://localhost:8080/api/v1/auth/register)
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registrasi berhasil!");
            response.put("username", registeredUser.getUsername());
            response.put("email", registeredUser.getEmail());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Endpoint login (POST http://localhost:8080/api/v1/auth/login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            String username = userRepository.findByEmail(loginRequest.getEmail())
                    .map(User::getUsername) // Mengambil string username jika user ditemukan
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan")); // Lempar error jika kosong

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login sukses!");
            response.put("token", token);
            response.put("token_type", "Bearer");
            response.put("username", username);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logout berhasil. Sesi Anda telah berakhir.");

        return ResponseEntity.ok(response);
    }
}