package com.warehouse.service;

import com.warehouse.dto.StockAdjustmentDTO;
import com.warehouse.entity.Item;
import com.warehouse.entity.Stock;
import com.warehouse.entity.ItemVariant;
import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.repository.ItemRepository;
import com.warehouse.repository.StockRepository;
import com.warehouse.repository.ItemVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

/**
 * Service layer for Stock management
 * ID Type: UUID (unique identifier, not numeric)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StockService {
    
    private final StockRepository stockRepository;
    private final ItemRepository itemRepository;
    private final ItemVariantRepository itemVariantRepository;
    
    /**
     * Get current stock for an item
     */
    public Map<String, Object> getItemStock(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));
        
        // Get all stock records for this item
        List<Stock> stocks = stockRepository.findByItem(item);
        
        int totalQuantityOnHand = 0;
        int totalQuantityReserved = 0;
        int totalQuantityAvailable = 0;
        
        for (Stock stock : stocks) {
            totalQuantityOnHand += stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
            totalQuantityReserved += stock.getQuantityReserved() != null ? stock.getQuantityReserved() : 0;
            totalQuantityAvailable += stock.getQuantityAvailable() != null ? stock.getQuantityAvailable() : 0;
        }
        
        int reorderLevel = 0;
        try {
            reorderLevel = Integer.parseInt(item.getReorderLevel());
        } catch (NumberFormatException e) {
            reorderLevel = 0;
        }
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("itemId", item.getId());
        response.put("itemName", item.getName());
        response.put("sku", item.getSku());
        response.put("reorderLevel", item.getReorderLevel());
        response.put("quantityOnHand", totalQuantityOnHand);
        response.put("quantityReserved", totalQuantityReserved);
        response.put("quantityAvailable", totalQuantityAvailable);
        response.put("isLowStock", totalQuantityOnHand < reorderLevel);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
    
    /**
     * Increase stock for an item
     */
    public Map<String, Object> increaseStock(StockAdjustmentDTO adjustmentDTO) {
        if (adjustmentDTO.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be positive for stock increase");
        }
        
        Item item = itemRepository.findById(adjustmentDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + adjustmentDTO.getItemId()));
        
        // Get or create stock record
        Stock stock = getOrCreateStock(item, adjustmentDTO.getVariantId());
        
        int oldQuantity = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
        int newQuantity = oldQuantity + adjustmentDTO.getQuantity();
        
        stock.setQuantityOnHand(newQuantity);
        stock.setQuantityAvailable(newQuantity - (stock.getQuantityReserved() != null ? stock.getQuantityReserved() : 0));
        stock.setLastMovementAt(LocalDateTime.now());
        
        Stock savedStock = stockRepository.save(stock);
        
        return buildAdjustmentResponse(savedStock, item, "INCREASE", adjustmentDTO.getReason(), 
                oldQuantity, newQuantity, adjustmentDTO.getNotes());
    }
    
    /**
     * Decrease stock for an item
     */
    public Map<String, Object> decreaseStock(StockAdjustmentDTO adjustmentDTO) {
        if (adjustmentDTO.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be positive for stock decrease");
        }
        
        Item item = itemRepository.findById(adjustmentDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + adjustmentDTO.getItemId()));
        
        // Get or create stock record
        Stock stock = getOrCreateStock(item, adjustmentDTO.getVariantId());
        
        int oldQuantity = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
        int newQuantity = oldQuantity - adjustmentDTO.getQuantity();
        
        // Check if stock would go negative
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock. Current: %d, Requested to remove: %d", 
                            oldQuantity, adjustmentDTO.getQuantity())
            );
        }
        
        stock.setQuantityOnHand(newQuantity);
        stock.setQuantityAvailable(newQuantity - (stock.getQuantityReserved() != null ? stock.getQuantityReserved() : 0));
        stock.setLastMovementAt(LocalDateTime.now());
        
        Stock savedStock = stockRepository.save(stock);
        
        return buildAdjustmentResponse(savedStock, item, "DECREASE", adjustmentDTO.getReason(), 
                oldQuantity, newQuantity, adjustmentDTO.getNotes());
    }
    
    /**
     * Generic adjust stock (positive = increase, negative = decrease)
     */
    public Map<String, Object> adjustStock(StockAdjustmentDTO adjustmentDTO) {
        Item item = itemRepository.findById(adjustmentDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + adjustmentDTO.getItemId()));
        
        // Get or create stock record
        Stock stock = getOrCreateStock(item, adjustmentDTO.getVariantId());
        
        int oldQuantity = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
        int newQuantity = oldQuantity + adjustmentDTO.getQuantity();
        
        // Check if stock would go negative
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock. Current: %d, Adjustment: %d", 
                            oldQuantity, adjustmentDTO.getQuantity())
            );
        }
        
        stock.setQuantityOnHand(newQuantity);
        stock.setQuantityAvailable(newQuantity - (stock.getQuantityReserved() != null ? stock.getQuantityReserved() : 0));
        stock.setLastMovementAt(LocalDateTime.now());
        
        Stock savedStock = stockRepository.save(stock);
        
        String type = adjustmentDTO.getQuantity() > 0 ? "INCREASE" : "DECREASE";
        
        return buildAdjustmentResponse(savedStock, item, type, adjustmentDTO.getReason(), 
                oldQuantity, newQuantity, adjustmentDTO.getNotes());
    }
    
    /**
     * Helper: Get or create stock record for item/variant
     * Handles DataIntegrityViolationException for duplicate (item_id, variant_id)
     */
    private Stock getOrCreateStock(Item item, UUID variantId) {
        List<Stock> existingStocks = stockRepository.findByItem(item);
        
        if (existingStocks.isEmpty()) {
            // Create default stock with first available variant
            List<ItemVariant> variants = itemVariantRepository.findByItemId(item.getId());
            
            if (variants.isEmpty()) {
                throw new IllegalArgumentException("No variant found for item: " + item.getId());
            }
            
            ItemVariant variant = variants.get(0);
            
            Stock newStock = Stock.builder()
                    .item(item)
                    .variant(variant)
                    .quantityOnHand(0)
                    .quantityReserved(0)
                    .build();
            
            try {
                return stockRepository.save(newStock);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("Stock already exists for item %s with variant %s", 
                                item.getId(), variant.getId()), e);
            }
        }
        
        // Return first stock record
        return existingStocks.get(0);
    }
    
    /**
     * Helper: Build adjustment response
     */
    private Map<String, Object> buildAdjustmentResponse(Stock stock, Item item, String type, 
                                                        String reason, int oldQuantity, int newQuantity, String notes) {
        int reorderLevel = 0;
        try {
            reorderLevel = Integer.parseInt(item.getReorderLevel());
        } catch (NumberFormatException e) {
            reorderLevel = 0;
        }
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("adjustmentType", type);
        response.put("itemId", item.getId());
        response.put("itemName", item.getName());
        response.put("sku", item.getSku());
        response.put("quantityBefore", oldQuantity);
        response.put("quantityAdjusted", Math.abs(newQuantity - oldQuantity));
        response.put("quantityAfter", newQuantity);
        response.put("reason", reason);
        response.put("notes", notes);
        response.put("isLowStock", newQuantity < reorderLevel);
        response.put("reorderLevel", item.getReorderLevel());
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
}
