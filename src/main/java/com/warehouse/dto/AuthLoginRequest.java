package com.warehouse.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Login request DTO
 * Untuk proses login user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthLoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}

