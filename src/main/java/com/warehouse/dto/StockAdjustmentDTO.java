package com.warehouse.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.annotation.*;
import java.util.UUID;

/**
 * DTO for stock adjustment requests
 * ID Type: UUID (unique identifier, not numeric)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentDTO {

    @NotNull(message = "Item ID is required")
    private UUID itemId;

    private UUID variantId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String notes;
}
