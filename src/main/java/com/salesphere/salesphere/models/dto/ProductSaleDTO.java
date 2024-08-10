package com.salesphere.salesphere.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSaleDTO {
    private Long id;
    private String productName;
    private Double salePrice;
    private Long quantity;

    public ProductSaleDTO(Long id, String productName,
                          Double salePrice, Long quantity) {
        this.id = id;
        this.productName = productName;
        this.salePrice = salePrice;
        this.quantity = quantity;
    }
}