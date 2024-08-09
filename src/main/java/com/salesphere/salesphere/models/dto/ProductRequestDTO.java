package com.salesphere.salesphere.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.models.enums.StatusEnum;

import java.time.LocalDate;
import java.util.List;

public record ProductRequestDTO(
        String productName,
        String description,
        String brand,
        CategoryEnum category,
        Double purchasePrice,
        Double salePrice,
        Long stockQuantity,
        Long minimumQuantity,
        @JsonProperty("code_sku") String codeSku,
        AvailabilityEnum availability,
        LocalDate expirationDate,
        StatusEnum status,
        List<Long> inventoryMovementIds,
        List<Long> warehouseProductIds,
        Long inventoryItemId
) {}