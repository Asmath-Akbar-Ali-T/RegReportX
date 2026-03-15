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

        boolean passwordMatches = false;
        if (user.getPassword() != null && user.getPassword().startsWith("$2a$")) {
            try {
                // Import org.mindrot.jbcrypt.BCrypt or use fully qualified name
                passwordMatches = org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword());
            } catch (Exception e) {
                passwordMatches = false;
            }
        } else {
            // Fallback for plain-text legacy passwords
            passwordMatches = user.getPassword().equals(password);
        }

        if (!passwordMatches) {
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