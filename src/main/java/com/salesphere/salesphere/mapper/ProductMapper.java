package com.salesphere.salesphere.mapper;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toProduct(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        return new Product(
                null,
                productRequestDTO.productName(),
                productRequestDTO.brand(),
                productRequestDTO.category(),
                productRequestDTO.purchasePrice(),
                productRequestDTO.salePrice(),
                productRequestDTO.stockQuantity(),
                productRequestDTO.minimumQuantity(),
                productRequestDTO.codeSKU()
        );
    }

    public ProductResponseDTO toProductResponse(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        return new ProductResponseDTO(
                product.getProductName(),
                product.getBrand(),
                product.getCategory(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getStockQuantity(),
                product.getMinimumQuantity(),
                product.getCodeSku()
        );
    }
}