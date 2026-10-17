package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ItemVariant Entity
 * Represents a variant of an item (e.g., size M - Red, size L - Blue)
 * ID: UUID (not numeric sequence)
 * Normalization: 3NF - No transitive dependencies
 */
@Entity
@Table(name = "item_variants", indexes = {
        @Index(name = "idx_item_id", columnList = "item_id"),
        @Index(name = "idx_sku", columnList = "sku", unique = true),
        @Index(name = "idx_active", columnList = "active"),
        @Index(name = "uq_item_variant", columnList = "item_id,color,size", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemVariant {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    // Variant Attributes
    @Column(length = 50)
    private String color;

    @Column(length = 50)
    private String size;

    @Column()
    private Double weight;  // in kg

    @Column(length = 50)
    private String dimensions;  // e.g., "10x20x30cm"

    // Pricing - Additive Model
    @Column(nullable = false)
    private Double priceModifier;  // Added to base_price

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate final price for this variant
     * @return base_price + price_modifier
     */
    public Double getFinalPrice() {
        if (item == null || item.getBasePrice() == null) {
            return priceModifier;
        }
        try {
            double basePrice = Double.parseDouble(item.getBasePrice());
            return basePrice + priceModifier;
        } catch (NumberFormatException e) {
            return priceModifier;
        }
    }

}
