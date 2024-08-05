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

    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Product toProduct(ProductRequestDTO productRequestDTO) {
        validateProductRequestDTO(productRequestDTO);

        Category category = getCategory(productRequestDTO.category());

        return new Product(
                null,
                productRequestDTO.productName(),
                productRequestDTO.description(),
                productRequestDTO.brand(),
                productRequestDTO.purchasePrice(),
                productRequestDTO.salePrice(),
                productRequestDTO.stockQuantity(),
                productRequestDTO.minimumQuantity(),
                productRequestDTO.codeSKU(),
                category,
                productRequestDTO.availability()
        );
    }

    public ProductResponseDTO toProductResponse(Product product) {
        validateProduct(product);

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

    private Category getCategory(CategoryEnum categoryEnum) {
        return categoryRepository.findByCategoryEnum(categoryEnum)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
    }

    private void validateProductRequestDTO(ProductRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("A solicitação não pode ser nula");
        }
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("O produto não pode ser nulo");
        }
    }

    public void updateProductFromDto(ProductRequestDTO dto, Product product) {
        validateProduct(product);
        Optional.ofNullable(dto.productName()).ifPresent(product::setProductName);
        Optional.ofNullable(dto.description()).ifPresent(product::setDescription);
        Optional.ofNullable(dto.brand()).ifPresent(product::setBrand);
        Optional.ofNullable(dto.category()).ifPresent(categoryEnum -> {
            Category category = getCategory(categoryEnum);
            product.setCategory(category);
        });
        Optional.ofNullable(dto.purchasePrice()).ifPresent(product::setPurchasePrice);
        Optional.ofNullable(dto.salePrice()).ifPresent(product::setSalePrice);
        Optional.ofNullable(dto.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(dto.minimumQuantity()).ifPresent(product::setMinimumQuantity);
        Optional.ofNullable(dto.codeSKU()).ifPresent(product::setCodeSku);
        Optional.ofNullable(dto.availability()).ifPresent(product::setAvailability);
    }
}