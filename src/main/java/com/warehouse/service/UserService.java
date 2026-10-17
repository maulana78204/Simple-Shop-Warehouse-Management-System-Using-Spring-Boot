package com.warehouse.service;

import com.warehouse.dto.AuthRegisterRequest;
import com.warehouse.entity.User;
import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Service - Handle user registration dan management
 * Sesuai dengan table: warehouse.users
 * ID Type: UUID (unique identifier, not numeric)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register user baru
     * Flow: Username & Email validation → Password encoding → Save to DB
     * Returns: User object
     * Throws: IllegalArgumentException jika username/email sudah ada
     */
    public User registerUser(AuthRegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        // Check duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username sudah terdaftar: " + request.getUsername());
        }

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email sudah terdaftar: " + request.getEmail());
        }

        // Create user dengan default role USER
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .accountLocked(false)
                .loginAttempts(0)
                .emailVerified(false)
                .role("ROLE_USER")
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new ResourceNotFoundException("User tidak ditemukan: " + username);
                });
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User tidak ditemukan dengan ID: " + id);
                });
    }

    /**
     * Update last login time
     */
    public void updateLastLoginTime(UUID userId) {
        try {
            userRepository.updateLastLoginTime(userId, LocalDateTime.now());
            log.debug("Updated last login time for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to update last login time for user ID: {}", userId, e);
            // Don't throw exception - non-critical operation
        }
    }

    /**
     * Check if username exists
     */
    @Transactional(readOnly = true)
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

}

