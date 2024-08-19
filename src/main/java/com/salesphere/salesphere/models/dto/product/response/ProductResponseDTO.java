package com.salesphere.salesphere.models.dto.product.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesphere.salesphere.models.product.Availability;
import com.salesphere.salesphere.models.product.Category;
import com.salesphere.salesphere.models.inventory.InventoryMovement;
import com.salesphere.salesphere.models.product.warehouse.WarehouseProduct;
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