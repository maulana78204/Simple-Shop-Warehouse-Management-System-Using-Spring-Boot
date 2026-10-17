package com.warehouse.repository;

import com.warehouse.entity.ItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ItemVariant entity
 * ID Type: UUID (unique identifier, not numeric)
 */
@Repository
public interface ItemVariantRepository extends JpaRepository<ItemVariant, UUID> {
    
    List<ItemVariant> findByItemId(UUID itemId);
    
    Optional<ItemVariant> findBySku(String sku);
    
    List<ItemVariant> findByActiveTrue();
}
