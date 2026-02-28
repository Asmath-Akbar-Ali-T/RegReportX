package com.cts.regreportx.controller;

import com.cts.regreportx.model.User;
import com.cts.regreportx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        if (!user.getRole().equals(role)) {
            return ResponseEntity.badRequest().body("Invalid role selected");
        }

        if (!user.getStatus().equals("ACTIVE")) {
            return ResponseEntity.badRequest().body("User inactive");
        }

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "role", user.getRole()
        ));
    }
}