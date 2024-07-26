package com.salesphere.salesphere.models.dto;

public record ProductResponseDTO(String productName,
                                 String brand,
                                 String category,
                                 Double purchasePrice,
                                 Double salePrice,
                                 Long stockQuantity,
                                 Long minimumQuantity,
                                 String codeSKU) {
}
