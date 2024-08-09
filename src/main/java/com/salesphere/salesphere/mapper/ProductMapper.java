package com.salesphere.salesphere.mapper;

import com.salesphere.salesphere.models.*;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.models.enums.StatusEnum;
import com.salesphere.salesphere.repositories.CategoryRepository;
import com.salesphere.salesphere.repositories.AvailabilityRepository;
import com.salesphere.salesphere.repositories.InventoryMovementRepository;
import com.salesphere.salesphere.repositories.WarehouseProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final CategoryRepository categoryRepository;
    private final AvailabilityRepository availabilityRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final WarehouseProductRepository warehouseProductRepository;

    @Autowired
    public ProductMapper(
            CategoryRepository categoryRepository,
            AvailabilityRepository availabilityRepository,
            InventoryMovementRepository inventoryMovementRepository,
            WarehouseProductRepository warehouseProductRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.availabilityRepository = availabilityRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.warehouseProductRepository = warehouseProductRepository;
    }

    public Product toProduct(ProductRequestDTO productRequestDTO) {
        validateProductRequestDTO(productRequestDTO);

        Category category = getCategory(productRequestDTO.category());
        Availability availability = getAvailability(productRequestDTO.availability());
        Set<InventoryMovement> inventoryMovements = getInventoryMovements(productRequestDTO.inventoryMovementIds());
        Set<WarehouseProduct> warehouseProducts = getWarehouseProducts(productRequestDTO.warehouseProductIds());

        Product product = new Product(
                null,
                productRequestDTO.productName(),
                productRequestDTO.description(),
                productRequestDTO.brand(),
                productRequestDTO.purchasePrice(),
                productRequestDTO.salePrice(),
                productRequestDTO.stockQuantity(),
                productRequestDTO.minimumQuantity(),
                productRequestDTO.codeSku(),
                productRequestDTO.inventoryItemId(),
                category,
                availability,
                productRequestDTO.expirationDate(),
                productRequestDTO.status(),
                inventoryMovements,
                warehouseProducts
        );

        inventoryMovements.forEach(movement -> movement.setProduct(product));
        warehouseProducts.forEach(warehouseProduct -> warehouseProduct.setProduct(product));

        return product;
    }


    private Set<InventoryMovement> getInventoryMovements(List<Long> inventoryMovementIds) {
        return inventoryMovementIds == null ? new HashSet<>() : inventoryMovementRepository.findAllById(inventoryMovementIds).stream().collect(Collectors.toSet());
    }

    private Set<WarehouseProduct> getWarehouseProducts(List<Long> warehouseProductIds) {
        return warehouseProductIds == null ? new HashSet<>() : warehouseProductRepository.findAllById(warehouseProductIds).stream().collect(Collectors.toSet());
    }

    public ProductResponseDTO toProductResponse(Product product) {
        validateProduct(product);

        return new ProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getDescription(),
                product.getBrand(),
                product.getCategory(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getStockQuantity(),
                product.getMinimumQuantity(),
                product.getCodeSku(),
                product.getInventoryItemId(), // Incluído aqui
                product.getAvailability(),
                product.getExpirationDate(),
                product.getStatus(),
                product.getInventoryMovements(),
                product.getWarehouseProducts()
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
        Optional.ofNullable(dto.codeSku()).ifPresent(product::setCodeSku);
        Optional.ofNullable(dto.availability()).ifPresent(availabilityEnum -> {
            Availability availability = getAvailability(availabilityEnum);
            product.setAvailability(availability);
        });
        Optional.ofNullable(dto.expirationDate()).ifPresent(product::setExpirationDate);
        Optional.ofNullable(dto.status()).ifPresent(product::setStatus);
        Optional.ofNullable(dto.inventoryItemId()).ifPresent(product::setInventoryItemId); // Atualização aqui
    }
}