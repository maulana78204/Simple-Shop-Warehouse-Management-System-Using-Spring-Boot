package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stock Entity - Real-time inventory tracking
 * ID: UUID (not numeric sequence)
 */
@Entity
@Table(name = "stock", indexes = {
        @Index(name = "idx_stock_item_id", columnList = "item_id"),
        @Index(name = "idx_stock_variant_id", columnList = "variant_id"),
        @Index(name = "idx_stock_quantity_available", columnList = "quantity_available"),
        @Index(name = "uq_stock_item_variant", columnList = "item_id,variant_id", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_stock_item_variant_unique", columnNames = {"item_id", "variant_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Stock {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ItemVariant variant;

    @Column(nullable = false)
    private Integer quantityOnHand;

    @Column(nullable = false)
    private Integer quantityReserved;

    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "last_movement_at")
    private LocalDateTime lastMovementAt;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (quantityOnHand == null) {
            quantityOnHand = 0;
        }
        if (quantityReserved == null) {
            quantityReserved = 0;
        }
        quantityAvailable = quantityOnHand - quantityReserved;
        lastMovementAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        quantityAvailable = quantityOnHand - quantityReserved;
        lastMovementAt = LocalDateTime.now();
    }
}
