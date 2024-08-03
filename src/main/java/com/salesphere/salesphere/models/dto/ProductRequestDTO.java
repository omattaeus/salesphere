package com.salesphere.salesphere.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;

public record ProductRequestDTO(String productName,
                                String description,
                                String brand,
                                CategoryEnum category,
                                Double purchasePrice,
                                Double salePrice,
                                Long stockQuantity,
                                Long minimumQuantity,
                                @JsonProperty("code_sku") String codeSKU,
                                AvailabilityEnum availability) {
}