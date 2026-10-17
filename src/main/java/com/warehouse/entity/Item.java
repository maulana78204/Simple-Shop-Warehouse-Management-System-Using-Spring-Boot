package com.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Item Entity
 * Represents a product sold in the warehouse
 * ID: UUID (not numeric sequence)
 */
@Entity
@Table(name = "items", indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_active", columnList = "active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Item {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 20)
    private String basePrice;

    @Column(nullable = false, length = 20)
    private String reorderLevel;

    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // Relationship with ItemVariants - cascade delete
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemVariant> itemVariants = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
