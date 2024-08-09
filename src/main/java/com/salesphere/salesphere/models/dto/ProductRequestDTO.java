package com.salesphere.salesphere.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.models.enums.StatusEnum;

import java.time.LocalDate;
import java.util.List;

public record ProductRequestDTO(
        @JsonProperty("product_name") String productName,
        String description,
        String brand,
        CategoryEnum category,
        @JsonProperty("purchase_price") Double purchasePrice,
        @JsonProperty("sale_price") Double salePrice,
        @JsonProperty("stock_quantity") Long stockQuantity,
        @JsonProperty("minimum_quantity") Long minimumQuantity,
        @JsonProperty("code_sku") String codeSku,
        AvailabilityEnum availability,
        @JsonProperty("expiration_date") LocalDate expirationDate,
        StatusEnum status,
        @JsonProperty("inventory_movement_ids")List<Long> inventoryMovementIds,
        @JsonProperty("warehouse_product_ids") List<Long> warehouseProductIds,
        @JsonProperty("inventory_item_id") Long inventoryItemId
) {}