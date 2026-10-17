package com.warehouse.service;

import com.warehouse.dto.ItemDTO;
import com.warehouse.entity.Item;
import com.warehouse.entity.Stock;
import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.mapper.ItemMapper;
import com.warehouse.repository.ItemRepository;
import com.warehouse.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Item business logic
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final StockRepository stockRepository;
    
    public List<ItemDTO> getAllItems() {
        return itemRepository.findByActiveTrue()
                .stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public ItemDTO getItemById(UUID id) {
        return itemRepository.findById(id)
                .map(itemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }
    
    public ItemDTO createItem(ItemDTO itemDTO) {
        // Check SKU uniqueness
        if (itemRepository.existsBySku(itemDTO.getSku())) {
            throw new IllegalArgumentException(
                    String.format("SKU already exists: '%s'. Item must have unique SKU.", itemDTO.getSku())
            );
        }
        
        Item item = itemMapper.toEntity(itemDTO);
        item.setActive(true);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDTO(savedItem);
    }
    
    public ItemDTO updateItem(UUID id, ItemDTO itemDTO) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        
        // Check if SKU is being changed to a different SKU
        if (itemDTO.getSku() != null && !item.getSku().equals(itemDTO.getSku())) {
            // Check if new SKU already exists
            if (itemRepository.existsBySku(itemDTO.getSku())) {
                throw new IllegalArgumentException(
                        String.format("SKU already exists: '%s'. Item must have unique SKU.", itemDTO.getSku())
                );
            }
        }
        
        itemMapper.updateEntityFromDTO(itemDTO, item);
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toDTO(updatedItem);
    }
    
    public void deleteItem(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        
        item.setActive(false);
        itemRepository.save(item);
    }
    
    public ItemDTO getItemBySku(String sku) {
        return itemRepository.findBySku(sku)
                .map(itemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with SKU: " + sku));
    }
    
    public List<ItemDTO> getLowStockItems() {
        // Get all active items with their reorder levels
        List<Item> activeItems = itemRepository.findByActiveTrue();
        
        return activeItems.stream()
                .filter(item -> {
                    try {
                        // Get reorder level
                        int reorderLevel = Integer.parseInt(item.getReorderLevel());
                        
                        // Get total available stock for this item (sum across all variants)
                        List<Stock> itemStocks = stockRepository.findByItemId(item.getId());
                        int totalAvailable = itemStocks.stream()
                                .mapToInt(stock -> stock.getQuantityAvailable() != null ? stock.getQuantityAvailable() : 0)
                                .sum();
                        
                        // Return items where available stock <= reorder level
                        return totalAvailable <= reorderLevel;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .map(itemMapper::toDTO)
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Replace item UUID - Change old UUID to new UUID
     * Used when user wants to update item and also change its UUID
     * This deletes the old item (and its variants and stock) first, then creates a new one with a fresh UUID
     */
    public java.util.Map<String, Object> replaceItemUUID(UUID oldId, ItemDTO itemDTO) {
        Item existingItem = itemRepository.findById(oldId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + oldId));
        
        // Delete all Stock entries for this item FIRST
        List<Stock> itemStocks = stockRepository.findByItemId(oldId);
        if (!itemStocks.isEmpty()) {
            stockRepository.deleteAll(itemStocks);
        }
        
        // Delete old item (which will also delete its variants via cascade)
        itemRepository.deleteById(oldId);
        
        // Now validate if new SKU is unique (old item is already deleted)
        if (itemRepository.existsBySku(itemDTO.getSku())) {
            throw new IllegalArgumentException(
                    String.format("SKU already exists: '%s'", itemDTO.getSku())
            );
        }
        
        // Create new item with potentially new UUID
        Item newItem = itemMapper.toEntity(itemDTO);
        newItem.setActive(true);
        Item savedItem = itemRepository.save(newItem);
        
        java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("success", true);
        response.put("oldUUID", oldId.toString());
        response.put("newUUID", savedItem.getId().toString());
        response.put("message", "Item UUID replaced successfully");
        response.put("item", itemMapper.toDTO(savedItem));
        
        return response;
    }
}
