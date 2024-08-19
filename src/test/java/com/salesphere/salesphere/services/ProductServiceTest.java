package com.salesphere.salesphere.services;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.product.Availability;
import com.salesphere.salesphere.models.product.Category;
import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.models.dto.product.request.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.product.response.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.repositories.product.ProductRepository;
import com.salesphere.salesphere.repositories.product.AvailabilityRepository;
import com.salesphere.salesphere.services.email.EmailService;
import com.salesphere.salesphere.services.product.ProductService;
import com.salesphere.salesphere.services.websocket.StockWebSocketHandler;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private EmailService emailService;

    @Mock
    private StockWebSocketHandler stockWebSocketHandler;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given there are products registered, when retrieving all products, then it should return all products")
    public void shouldReturnAllProducts() {
        // Given
        Product product = new Product();
        product.setProductName("Tênis Nike Air");
        product.setAvailability(new Availability());

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                null, "Tênis Nike Air", "", "", CategoryEnum.SHOES, 0.0, 0.0, 0L, 0L, "", AvailabilityEnum.AVAILABLE
        );

        when(repository.findAll()).thenReturn(Collections.singletonList(product));
        when(productMapper.toProductResponse(product)).thenReturn(productResponseDTO);

        // When
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Then
        assertEquals(1, result.size());
        assertEquals(productResponseDTO, result.get(0));
    }

    @Test
    @DisplayName("Given valid product data, when creating a product, then it should create and return the product")
    public void shouldCreateProduct() {
        // Given
        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "Camiseta Puma", "Camiseta esportiva de algodão", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        Category maleCategory = new Category();
        maleCategory.setCategoryEnum(CategoryEnum.MALE);
        maleCategory.setId(1L);

        Product product = new Product();
        product.setProductName("Camiseta Puma");
        product.setCategory(maleCategory);
        product.setAvailability(new Availability());

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                null, "Camiseta Puma", "Camiseta esportiva de algodão", "Puma", CategoryEnum.MALE, 50.00, 80.00, 30L, 5L, "PUMA123", AvailabilityEnum.AVAILABLE
        );

        when(productMapper.toProduct(productRequestDTO)).thenReturn(product);
        when(availabilityRepository.findByAvailability(productRequestDTO.availability()))
                .thenReturn(Optional.of(new Availability()));
        when(repository.save(product)).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(productResponseDTO);

        // When
        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        // Then
        assertEquals(productResponseDTO, result);
    }

    @Test
    @DisplayName("Given valid and invalid product data, when creating multiple products, then it should create valid products and throw an exception for invalid data")
    public void shouldThrowValidationExceptionWhenOneProductHasInvalidData() {
        // Given
        ProductRequestDTO validProductRequestDTO = new ProductRequestDTO(
                "Camiseta Nike", "Camiseta esportiva", "Nike",
                CategoryEnum.MALE, 30.00, 50.00, 20L, 10L,
                "NIKE123", AvailabilityEnum.AVAILABLE
        );

        ProductRequestDTO invalidProductRequestDTO = new ProductRequestDTO(
                "", null, "Adidas",
                CategoryEnum.FEMALE, 120.00, 150.00, 15L, 5L,
                "ADIDAS456", AvailabilityEnum.OUT_OF_STOCK
        );

        when(productMapper.toProduct(validProductRequestDTO)).thenReturn(new Product());
        when(availabilityRepository.findByAvailability(validProductRequestDTO.availability())).thenReturn(Optional.of(new Availability()));
        when(productMapper.toProduct(invalidProductRequestDTO)).thenThrow(new ValidationException("Nome do produto é obrigatório"));

        List<ProductRequestDTO> requestDTOs = List.of(validProductRequestDTO, invalidProductRequestDTO);

        // When / Then
        assertThrows(ValidationException.class, () -> {
            productService.createProducts(requestDTOs);
        });
    }

    @Test
    @DisplayName("Given a product with invalid availability, when creating multiple products, then it should throw ValidationException")
    public void shouldThrowValidationExceptionWhenAvailabilityNotFound() {
        // Given
        ProductRequestDTO validProductRequestDTO = new ProductRequestDTO(
                "Camiseta Nike", "Camiseta esportiva", "Nike",
                CategoryEnum.MALE, 30.00, 50.00, 20L, 10L,
                "NIKE123", AvailabilityEnum.AVAILABLE
        );

        ProductRequestDTO anotherProductRequestDTO = new ProductRequestDTO(
                "Tênis Adidas", "Tênis esportivo", "Adidas",
                CategoryEnum.FEMALE, 120.00, 150.00, 15L, 5L,
                "ADIDAS456", AvailabilityEnum.OUT_OF_STOCK
        );

        when(productMapper.toProduct(validProductRequestDTO)).thenReturn(new Product());
        when(availabilityRepository.findByAvailability(validProductRequestDTO.availability())).thenReturn(Optional.of(new Availability()));
        when(productMapper.toProduct(anotherProductRequestDTO)).thenReturn(new Product());
        when(availabilityRepository.findByAvailability(anotherProductRequestDTO.availability())).thenReturn(Optional.empty());

        List<ProductRequestDTO> requestDTOs = List.of(validProductRequestDTO, anotherProductRequestDTO);

        // When / Then
        assertThrows(ValidationException.class, () -> {
            productService.createProducts(requestDTOs);
        });
    }

    @Test
    @DisplayName("Given invalid product data, when creating a product, then it should throw ValidationException")
    public void shouldThrowValidationExceptionWhenCreatingProductWithInvalidData() {
        // Given
        ProductRequestDTO invalidProductRequestDTO = new ProductRequestDTO(
                "", null, "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        // When / Then
        assertThrows(ValidationException.class, () -> {
            productService.createProduct(invalidProductRequestDTO);
        });
    }

    @Test
    @DisplayName("Given products with low stock, when retrieving products with low stock, then it should return products with low stock")
    public void shouldReturnProductsWithLowStock() {
        // Given
        Product productLowStock = new Product();
        productLowStock.setProductName("Tênis Nike Air Max");
        productLowStock.setStockQuantity(3L);
        productLowStock.setMinimumQuantity(5L);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                null, "Tênis Nike Air Max", "", "", CategoryEnum.SHOES, 0.0, 0.0, 3L, 5L, "", AvailabilityEnum.AVAILABLE
        );

        when(repository.findProductsWithLowStock()).thenReturn(Collections.singletonList(productLowStock));
        when(productMapper.toProductResponse(productLowStock)).thenReturn(productResponseDTO);

        // When
        List<ProductResponseDTO> result = productService.getProductsWithLowStock();

        // Then
        assertEquals(1, result.size());
        assertEquals(productResponseDTO, result.get(0));
    }

    @Test
    @DisplayName("Given products with low stock, when checking stock, then it should send a low stock alert")
    public void shouldSendLowStockAlertForLowStockProducts() {
        // Given
        Product productLowStock = new Product();
        productLowStock.setProductName("Produto com Baixo Estoque");
        productLowStock.setStockQuantity(3L);
        productLowStock.setMinimumQuantity(5L);

        when(repository.findProductsWithLowStock()).thenReturn(Collections.singletonList(productLowStock));

        // When
        productService.checkStock();

        // Then
        verify(emailService, times(1)).sendLowStockAlert(anyList());
    }

    @Test
    @DisplayName("Given product data, when updating a product, then it should update and return the product")
    public void shouldUpdateProduct() {
        // Given
        Long productId = 1L;
        ProductRequestDTO updateRequestDTO = new ProductRequestDTO(
                "Camiseta Atualizada", "Descrição atualizada", "Marca Atualizada",
                CategoryEnum.FEMALE, 60.00, 90.00, 25L, 10L,
                "CAMISETA123", AvailabilityEnum.OUT_OF_STOCK
        );

        Product existingProduct = new Product();
        existingProduct.setProductName("Camiseta Antiga");
        existingProduct.setCodeSku("OLDSKU");
        existingProduct.setPurchasePrice(50.00);
        existingProduct.setSalePrice(75.00);
        existingProduct.setStockQuantity(20L);
        existingProduct.setMinimumQuantity(5L);
        existingProduct.setAvailability(new Availability(AvailabilityEnum.AVAILABLE)); // Ajuste para AvailabilityEnum.AVAILABLE

        Product updatedProduct = new Product();
        updatedProduct.setProductName("Camiseta Atualizada");
        updatedProduct.setCodeSku("CAMISETA123");
        updatedProduct.setPurchasePrice(60.00);
        updatedProduct.setSalePrice(90.00);
        updatedProduct.setStockQuantity(25L);
        updatedProduct.setMinimumQuantity(10L);
        updatedProduct.setAvailability(new Availability(AvailabilityEnum.OUT_OF_STOCK)); // Ajuste para AvailabilityEnum.OUT_OF_STOCK

        ProductResponseDTO updatedProductResponseDTO = new ProductResponseDTO(
                productId, "Camiseta Atualizada", "Descrição atualizada", "Marca Atualizada", CategoryEnum.FEMALE, 60.00, 90.00, 25L, 10L, "CAMISETA123", AvailabilityEnum.OUT_OF_STOCK
        );

        when(repository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productMapper.toProduct(updateRequestDTO)).thenReturn(updatedProduct);
        when(availabilityRepository.findByAvailability(AvailabilityEnum.OUT_OF_STOCK))
                .thenReturn(Optional.of(new Availability(AvailabilityEnum.OUT_OF_STOCK)));
        when(repository.save(existingProduct)).thenReturn(updatedProduct); // Corrigido: Salvar existingProduct
        when(productMapper.toProductResponse(updatedProduct)).thenReturn(updatedProductResponseDTO);

        // When
        ProductResponseDTO result = productService.updateProduct(productId, updateRequestDTO);

        // Then
        assertNotNull(result, "O resultado não deve ser nulo");
        assertEquals(updatedProductResponseDTO, result);
    }

    @Test
    @DisplayName("Given a non-existent product ID, when updating a product, then it should throw ResourceNotFoundException")
    public void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentProduct() {
        // Given
        Long nonExistentProductId = 999L;
        ProductRequestDTO updateRequestDTO = new ProductRequestDTO(
                "Camiseta Atualizada", "Descrição atualizada", "Marca Atualizada",
                CategoryEnum.FEMALE, 60.00, 90.00, 25L, 10L,
                "CAMISETA123", AvailabilityEnum.OUT_OF_STOCK
        );

        when(repository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResponseStatusException.class, () -> {
            productService.updateProduct(nonExistentProductId, updateRequestDTO);
        });
    }

    @Test
    @DisplayName("Given a product ID, when deleting a product, then it should delete the product")
    public void shouldDeleteProduct() {
        // Given
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setProductName("Produto a ser excluído");

        when(repository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(repository.existsById(productId)).thenReturn(true);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(repository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Given a non-existent product ID, when deleting a product, then it should throw ResourceNotFoundException")
    public void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentProduct() {
        // Given
        Long nonExistentProductId = 999L;

        when(repository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProduct(nonExistentProductId);
        });
    }
}