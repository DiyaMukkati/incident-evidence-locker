package com.internship.tool.controller;

import com.internship.tool.config.JwtUtil;
import com.internship.tool.entity.User;
import com.internship.tool.exception.Exceptions.BadRequestException;
import com.internship.tool.exception.Exceptions.ConflictException;
import com.internship.tool.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller — login, register, refresh
 * Base URL: /api/auth
 * Day 5 — Java Developer 1
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ── POST /auth/register ───────────────────────────────────────
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        // Save new user with hashed password
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role("USER")
                .build();

        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(token, user.getUsername(), user.getRole()));
    }

    // ── POST /auth/login ──────────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        // Find user by username
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException(
                        "Invalid username or password"));

        // Check password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(token, user.getUsername(), user.getRole()));
    }

    // ── Request / Response records ────────────────────────────────

    public record RegisterRequest(
            String username,
            String email,
            String password
    ) {}

    public record LoginRequest(
            String username,
            String password
    ) {}

    public record AuthResponse(
            String token,
            String username,
            String role
    ) {}
}
