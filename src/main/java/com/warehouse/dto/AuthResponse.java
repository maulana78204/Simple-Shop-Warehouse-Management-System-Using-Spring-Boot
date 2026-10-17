package com.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Auth Response DTO
 * Response untuk register dan login
 * ID Type: UUID (unique identifier, not numeric)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private UUID id;

    private String username;

    private String email;

    private String role;

    private Boolean active;

    private String message;

    private String status; // SUCCESS, ERROR, etc

}

