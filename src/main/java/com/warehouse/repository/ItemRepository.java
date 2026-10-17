package com.warehouse.repository;

import com.warehouse.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.UUID;

/**
 * Repository for Item entity
 * ID Type: UUID (unique identifier, not numeric)
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    
    Optional<Item> findBySku(String sku);
    
    List<Item> findByActiveTrue();
    
    List<Item> findByCategory(String category);
    
    boolean existsBySku(String sku);
    
    long countByCategory(String category);
}
