package com.salesphere.salesphere.mapper;

import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    public Product toProduct(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO == null) {
            throw new IllegalArgumentException("A solicitação não pode ser nula");
        }

        CategoryEnum categoryEnum = productRequestDTO.category();
        Category category = categoryRepository.findByCategoryEnum(categoryEnum)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        return new Product(
                null,
                productRequestDTO.productName(),
                productRequestDTO.description(),
                productRequestDTO.brand(),
                category,
                productRequestDTO.purchasePrice(),
                productRequestDTO.salePrice(),
                productRequestDTO.stockQuantity(),
                productRequestDTO.minimumQuantity(),
                productRequestDTO.codeSKU()
        );
    }

    public ProductResponseDTO toProductResponse(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo");
        }

        return new ProductResponseDTO(
                product.getProductName(),
                product.getDescription(),
                product.getBrand(),
                product.getCategory().getCategoryEnum(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getStockQuantity(),
                product.getMinimumQuantity(),
                product.getCodeSku()
        );
    }
}