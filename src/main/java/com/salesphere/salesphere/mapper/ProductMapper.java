package com.salesphere.salesphere.mapper;

import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                productRequestDTO.codeSKU(),
                productRequestDTO.availability()
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
                product.getCodeSku(),
                product.getAvailability()
        );
    }

    private void updateCategory(ProductRequestDTO dto, Product product) {
        Optional.ofNullable(dto.category()).ifPresent(categoryEnum -> {
            Category category = categoryRepository.findByCategoryEnum(categoryEnum)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
            product.setCategory(category);
        });
    }

    public void updateProductFromDto(ProductRequestDTO dto, Product product) {
        Optional.ofNullable(dto.productName()).ifPresent(product::setProductName);
        Optional.ofNullable(dto.description()).ifPresent(product::setDescription);
        Optional.ofNullable(dto.brand()).ifPresent(product::setBrand);
        updateCategory(dto, product);
        Optional.ofNullable(dto.purchasePrice()).ifPresent(product::setPurchasePrice);
        Optional.ofNullable(dto.salePrice()).ifPresent(product::setSalePrice);
        Optional.ofNullable(dto.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(dto.minimumQuantity()).ifPresent(product::setMinimumQuantity);
        Optional.ofNullable(dto.codeSKU()).ifPresent(product::setCodeSku);
        Optional.ofNullable(dto.availability()).ifPresent(product::setAvailability);
    }
}