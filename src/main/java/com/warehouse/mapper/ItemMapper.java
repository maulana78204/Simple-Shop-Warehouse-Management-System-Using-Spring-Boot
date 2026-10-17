package com.warehouse.mapper;

import com.warehouse.dto.ItemDTO;
import com.warehouse.entity.Item;
import org.springframework.stereotype.Component;

/**
 * Mapper for Item entity <-> ItemDTO conversion
 */
@Component
public class ItemMapper {
    
    public ItemDTO toDTO(Item entity) {
        if (entity == null) {
            return null;
        }
        
        return ItemDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sku(entity.getSku())
                .basePrice(entity.getBasePrice())
                .reorderLevel(entity.getReorderLevel())
                .category(entity.getCategory())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }
    
    public Item toEntity(ItemDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // For CREATE operations: don't set id, let database generate it
        // id, createdAt, updatedAt, version are handled by entity lifecycle
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .basePrice(dto.getBasePrice())
                .reorderLevel(dto.getReorderLevel())
                .category(dto.getCategory())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }
    
    /**
     * Merge DTO changes into existing entity (for UPDATE operations)
     * Preserves id, createdAt, version from existing entity
     */
    public void updateEntityFromDTO(ItemDTO dto, Item entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getSku() != null) {
            entity.setSku(dto.getSku());
        }
        if (dto.getBasePrice() != null) {
            entity.setBasePrice(dto.getBasePrice());
        }
        if (dto.getReorderLevel() != null) {
            entity.setReorderLevel(dto.getReorderLevel());
        }
        if (dto.getCategory() != null) {
            entity.setCategory(dto.getCategory());
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
