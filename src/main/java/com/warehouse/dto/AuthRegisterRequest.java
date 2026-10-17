package com.warehouse.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Register request DTO
 * Untuk proses registrasi user baru
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

