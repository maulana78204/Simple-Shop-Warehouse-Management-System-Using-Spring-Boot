package com.warehouse.controller;

import com.warehouse.dto.StockAdjustmentDTO;
import com.warehouse.dto.ItemDTO;
import com.warehouse.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

/**
 * StockController - REST API for Stock management
 * Endpoints for stock adjustment (add/reduce)
 * Role: ADMIN only
 * ID Type: UUID (unique identifier, not numeric)
 */
@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * GET /stock/item/{itemId} - Get current stock for an item (USER & ADMIN)
     */
    @GetMapping("/item/{itemId}")
    public ResponseEntity<Map<String, Object>> getItemStock(@PathVariable UUID itemId) {
        Map<String, Object> stockInfo = stockService.getItemStock(itemId);
        return ResponseEntity.ok(stockInfo);
    }

    /**
     * POST /stock/increase - Increase stock for an item (ADMIN only)
     */
    @PostMapping("/increase")
    public ResponseEntity<Map<String, Object>> increaseStock(@Valid @RequestBody StockAdjustmentDTO adjustmentDTO) {
        Map<String, Object> result = stockService.increaseStock(adjustmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * POST /stock/decrease - Decrease stock for an item (ADMIN only)
     */
    @PostMapping("/decrease")
    public ResponseEntity<Map<String, Object>> decreaseStock(@Valid @RequestBody StockAdjustmentDTO adjustmentDTO) {
        Map<String, Object> result = stockService.decreaseStock(adjustmentDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /stock/adjust - Generic stock adjustment (add/minus based on quantity) (ADMIN only)
     * Positive quantity = increase, Negative quantity = decrease
     */
    @PostMapping("/adjust")
    public ResponseEntity<Map<String, Object>> adjustStock(@Valid @RequestBody StockAdjustmentDTO adjustmentDTO) {
        Map<String, Object> result = stockService.adjustStock(adjustmentDTO);
        return ResponseEntity.ok(result);
    }

}
