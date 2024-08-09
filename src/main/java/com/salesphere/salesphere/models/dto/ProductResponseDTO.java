package com.salesphere.salesphere.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesphere.salesphere.models.Availability;
import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.InventoryMovement;
import com.salesphere.salesphere.models.WarehouseProduct;
import com.salesphere.salesphere.models.enums.StatusEnum;

import java.time.LocalDate;
import java.util.Set;

public record ProductResponseDTO(
        Long id,
        String productName,
        String description,
        String brand,
        Category category,
        Double purchasePrice,
        Double salePrice,
        Long stockQuantity,
        Long minimumQuantity,
        @JsonProperty("code_sku") String codeSku,
        Long inventoryItemId,
        Availability availability,
        LocalDate expirationDate,
        StatusEnum status,
        Set<InventoryMovement> inventoryMovements,
        Set<WarehouseProduct> warehouseProducts
) {}