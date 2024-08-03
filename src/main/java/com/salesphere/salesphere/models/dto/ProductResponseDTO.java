package com.salesphere.salesphere.models.dto;

import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;

public record ProductResponseDTO(String productName,
                                 String description,
                                 String brand,
                                 CategoryEnum category,
                                 Double purchasePrice,
                                 Double salePrice,
                                 Long stockQuantity,
                                 Long minimumQuantity,
                                 String codeSKU,
                                 AvailabilityEnum availability) {
}