package com.salesphere.salesphere.mapper;

import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.Availability;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.repositories.CategoryRepository;
import com.salesphere.salesphere.repositories.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductMapper {

    private final CategoryRepository categoryRepository;
    private final AvailabilityRepository availabilityRepository;

    @Autowired
    public ProductMapper(CategoryRepository categoryRepository, AvailabilityRepository availabilityRepository) {
        this.categoryRepository = categoryRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public Product toProduct(ProductRequestDTO productRequestDTO) {
        validateProductRequestDTO(productRequestDTO);

        Category category = getCategory(productRequestDTO.category());
        Availability availability = getAvailability(productRequestDTO.availability());

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
                availability
        );
    }

    public ProductResponseDTO toProductResponse(Product product) {
        validateProduct(product);

        return new ProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getBrand(),
                product.getCategory().getCategoryEnum(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getStockQuantity(),
                product.getMinimumQuantity(),
                product.getCodeSku(),
                product.getAvailability().getAvailability()
        );
    }

    private Category getCategory(CategoryEnum categoryEnum) {
        return categoryRepository.findByCategoryEnum(categoryEnum)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
    }

    private Availability getAvailability(AvailabilityEnum availabilityEnum) {
        return availabilityRepository.findByAvailability(availabilityEnum)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidade não encontrada"));
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
        Optional.ofNullable(dto.availability()).ifPresent(availabilityEnum -> {
            Availability availability = getAvailability(availabilityEnum);
            product.setAvailability(availability);
        });
    }
}