package com.warehouse.controller;

import com.warehouse.dto.ItemDTO;
import com.warehouse.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.Map;

/**
 * ItemController - REST API for Item management
 * Endpoints for CRUD operations on warehouse items
 * ID Type: UUID (unique identifier, not numeric)
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * GET /items - Retrieve all items (USER & ADMIN)
     */
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<ItemDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    /**
     * GET /items/{id} - Retrieve item by ID (USER & ADMIN)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable UUID id) {
        ItemDTO item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    /**
     * POST /items - Create new item (ADMIN only)
     */
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        ItemDTO created = itemService.createItem(itemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /items/{id} - Update item (ADMIN only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable UUID id, @Valid @RequestBody ItemDTO itemDTO) {
        ItemDTO updated = itemService.updateItem(id, itemDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * PUT /items/{id}/replace-uuid - Replace old UUID with new UUID (ADMIN only)
     * Allows changing item UUID and all its data in one operation
     */
    @PutMapping("/{id}/replace-uuid")
    public ResponseEntity<Map<String, Object>> replaceItemUUID(
            @PathVariable UUID id, 
            @Valid @RequestBody ItemDTO itemDTO) {
        Map<String, Object> result = itemService.replaceItemUUID(id, itemDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /items/{id} - Delete item (ADMIN only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /items/search/by-sku - Search items by SKU (USER & ADMIN)
     */
    @GetMapping("/search/by-sku")
    public ResponseEntity<ItemDTO> getItemBySku(@RequestParam String sku) {
        ItemDTO item = itemService.getItemBySku(sku);
        return ResponseEntity.ok(item);
    }

    /**
     * GET /items/stock/low-stock - Get items with low stock (USER & ADMIN)
     */
    @GetMapping("/stock/low-stock")
    public ResponseEntity<List<ItemDTO>> getLowStockItems() {
        List<ItemDTO> items = itemService.getLowStockItems();
        return ResponseEntity.ok(items);
    }

}
