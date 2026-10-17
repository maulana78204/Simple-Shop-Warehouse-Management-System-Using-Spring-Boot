package com.warehouse.controller;

import com.warehouse.dto.AuthLoginRequest;
import com.warehouse.dto.AuthRegisterRequest;
import com.warehouse.dto.AuthResponse;
import com.warehouse.entity.User;
import com.warehouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Authentication Controller
 * ID Type: UUID (unique identifier, not numeric)
 * 
 * ENDPOINTS:
 * - POST /auth/register (PUBLIC) - USER can create own account
 * - POST /auth/login (PUBLIC) - USER can login
 * - GET /auth/me (ADMIN ONLY) - ADMIN can view profile
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register user baru (PUBLIC)
     * 
     * USER dapat membuat akun dengan username dan password sendiri
     * 
     * @param request - Username, email, password
     * @return 201 Created dengan user info
     */
    @PostMapping("/register")
    @Operation(summary = "Register akun baru (PUBLIC)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        try {
            log.info("Register request for username: {}", request.getUsername());

            User user = userService.registerUser(request);

            AuthResponse response = AuthResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .active(user.getActive())
                    .status("SUCCESS")
                    .message("Registrasi berhasil! Silahkan login dengan username dan password Anda.")
                    .build();

            log.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.builder()
                            .status("ERROR")
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .status("ERROR")
                            .message("Terjadi kesalahan saat registrasi")
                            .build());
        }
    }

    /**
     * Login user (PUBLIC)
     * 
     * USER dapat login dengan username dan password
     * 
     * @param request - Username, password
     * @return 200 OK dengan user info
     */
    @PostMapping("/login")
    @Operation(summary = "Login (PUBLIC)")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        try {
            log.info("Login request for username: {}", request.getUsername());

            // Authenticate dengan Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Get user dari database
            User user = userService.getUserByUsername(request.getUsername());

            // Check jika user active
            if (!user.getActive()) {
                log.warn("Login attempt for inactive user: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(AuthResponse.builder()
                                .status("ERROR")
                                .message("User account tidak aktif")
                                .build());
            }

            // Check jika user account locked
            if (user.getAccountLocked()) {
                log.warn("Login attempt for locked user: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(AuthResponse.builder()
                                .status("ERROR")
                                .message("User account terkunci. Silahkan hubungi administrator")
                                .build());
            }

            // Update last login time
            userService.updateLastLoginTime(user.getId());

            // Return response
            AuthResponse response = AuthResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .active(user.getActive())
                    .status("SUCCESS")
                    .message("Login berhasil! Selamat datang " + user.getUsername())
                    .build();

            log.info("User logged in successfully: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Login failed - bad credentials for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .status("ERROR")
                            .message("Username atau password salah")
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .status("ERROR")
                            .message("Login gagal. Periksa kembali username dan password Anda")
                            .build());
        }
    }

    /**
     * Get ADMIN profile (ADMIN ONLY)
     * 
     * Hanya ADMIN yang sudah pre-seeded yang bisa akses
     * USER tidak bisa akses endpoint ini
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get ADMIN profile (ADMIN ONLY)")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            User user = userService.getUserByUsername(authentication.getName());
            AuthResponse response = AuthResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .active(user.getActive())
                    .status("SUCCESS")
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

