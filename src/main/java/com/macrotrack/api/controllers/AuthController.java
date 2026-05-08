package com.macrotrack.api.controllers;

import com.macrotrack.api.dto.AuthResponse;
import com.macrotrack.api.dto.LoginRequest;
import com.macrotrack.api.dto.SignupRequest;
import com.macrotrack.api.entity.User;
import com.macrotrack.api.repository.UserRepository;
import com.macrotrack.api.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getDailyCalorieGoal() != null) {
            user.setDailyCalorieGoal(request.getDailyCalorieGoal());
        }
        userRepository.save(user);

        String token = jwtUtil.generateToken(
            user.getId().toString(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(
            token, user.getId().toString(), user.getEmail(),
            user.getName(), user.getDailyCalorieGoal()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }

        String token = jwtUtil.generateToken(
            user.getId().toString(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(
            token, user.getId().toString(), user.getEmail(),
            user.getName(), user.getDailyCalorieGoal()));
    }
}
