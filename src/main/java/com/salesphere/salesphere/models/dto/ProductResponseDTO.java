package com.salesphere.salesphere.models.dto;

import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;

public record ProductResponseDTO(Long id,
                                String productName,
                                String description,
                                String brand,
                                CategoryEnum category,
                                Double purchasePrice,
                                Double retailPrice,
                                Long stockQuantity,
                                Long minimumStock,
                                String sku,
                                AvailabilityEnum availability
                        ) {}