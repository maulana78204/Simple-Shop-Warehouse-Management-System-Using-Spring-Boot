package com.warehouse.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Item Data Transfer Object
 * Used for API request/response
 * ID: UUID (unique identifier, not numeric)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank(message = "Item name is required")
    @Size(min = 3, max = 100, message = "Item name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens")
    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    private String sku;

    @NotBlank(message = "Base price is required")
    @Size(min = 1, max = 20, message = "Base price must be between 1 and 20 characters")
    private String basePrice;

    @NotBlank(message = "Reorder level is required")
    @Size(min = 1, max = 20, message = "Reorder level must be between 1 and 20 characters")
    private String reorderLevel;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @Builder.Default
    private Boolean active = true;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long version;

}
