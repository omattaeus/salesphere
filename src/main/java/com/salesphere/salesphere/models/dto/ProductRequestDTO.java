package com.salesphere.salesphere.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductRequestDTO(String productName,
                                String brand,
                                String category,
                                Double purchasePrice,
                                Double salePrice,
                                Long stockQuantity,
                                Long minimumQuantity,
                                @JsonProperty("code_sku") String codeSKU) {
}
