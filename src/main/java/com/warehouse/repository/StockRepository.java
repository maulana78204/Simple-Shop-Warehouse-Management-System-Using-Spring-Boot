package com.warehouse.repository;

import com.warehouse.entity.Stock;
import com.warehouse.entity.Item;
import com.warehouse.entity.ItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Stock entity
 * ID Type: UUID (unique identifier, not numeric)
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {
    
    Optional<Stock> findByItemAndVariant(Item item, ItemVariant variant);
    
    List<Stock> findByItemId(UUID itemId);
    
    List<Stock> findByItem(Item item);
    
    List<Stock> findByVariantId(UUID variantId);
    
    List<Stock> findByQuantityAvailableLessThan(Integer quantity);
}
