package com.salesphere.salesphere.services;

import com.salesphere.salesphere.mapper.ProductMapper;
import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("You must return all products when there are registered products")
    public void shouldReturnAllProducts() {

        Category shoesCategory = new Category();
        shoesCategory.setCategoryEnum(CategoryEnum.SHOES);
        shoesCategory.setId(1L);

        Product product = new Product();
        product.setProductName("Tênis Nike Air");
        product.setDescription("Tênis esportivo");
        product.setBrand("Nike");
        product.setCategory(shoesCategory);
        product.setPurchasePrice(250.00);
        product.setSalePrice(400.00);
        product.setStockQuantity(10L);
        product.setMinimumQuantity(2L);
        product.setCodeSku("NIKE123");
        product.setAvailability(AvailabilityEnum.AVAILABLE);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                "Tênis Nike Air", "Tênis esportivo", "Nike",
                CategoryEnum.SHOES, 250.00, 400.00, 10L, 2L,
                "NIKE123", AvailabilityEnum.AVAILABLE
        );

        when(repository.findAll()).thenReturn(Collections.singletonList(product));
        when(productMapper.toProductResponse(product)).thenReturn(productResponseDTO);

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals(productResponseDTO, result.get(0));
    }

    @Test
    @DisplayName("You must create a new product when the data is valid")
    public void shouldCreateProduct() {

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
        product.setDescription("Camiseta esportiva de algodão");
        product.setBrand("Puma");
        product.setCategory(maleCategory);
        product.setPurchasePrice(50.00);
        product.setSalePrice(80.00);
        product.setStockQuantity(30L);
        product.setMinimumQuantity(5L);
        product.setCodeSku("PUMA123");
        product.setAvailability(AvailabilityEnum.AVAILABLE);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                "Camiseta Puma", "Camiseta esportiva de algodão", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        when(productMapper.toProduct(productRequestDTO)).thenReturn(product);
        when(repository.save(product)).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertEquals(productResponseDTO, result);
    }

    @Test
    @DisplayName("You must return products with low stock when there are products below the minimum stock")
    public void shouldReturnProductsWithLowStock() {

        Product productLowStock = new Product();
        productLowStock.setProductName("Tênis Nike Air Max");
        productLowStock.setStockQuantity(3L);
        productLowStock.setMinimumQuantity(5L);

        when(repository.findProductsWithLowStock()).thenReturn(Collections.singletonList(productLowStock));

        List<Product> result = productService.getProductsWithLowStock();

        assertEquals(1, result.size(), "Esperado um produto com estoque baixo");
        assertTrue(result.contains(productLowStock), "Esperado que productLowStock esteja no resultado");
    }

    @Test
    @DisplayName("Should send low stock alert for products with quantities below the minimum")
    public void shouldSendLowStockAlertForLowStockProducts() throws Exception {

        Product productLowStock = new Product();
        productLowStock.setProductName("Low Stock Product");
        productLowStock.setStockQuantity(3L);
        productLowStock.setMinimumQuantity(5L);

        when(repository.findProductsWithLowStock()).thenReturn(Collections.singletonList(productLowStock));

        productService.checkStock();

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should return false when there are no products with low stock")
    public void shouldReturnFalseWhenNoLowStockProducts() {
        when(repository.findProductsWithLowStock()).thenReturn(Collections.emptyList());

        boolean result = productService.checkStock();

        assertFalse(result, "Esperado que o resultado seja falso quando não houver produtos com estoque baixo");
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should return true when there are products with low stock")
    public void shouldReturnTrueWhenLowStockProducts() {
        Product productLowStock = new Product();
        productLowStock.setProductName("Low Stock Product");
        productLowStock.setStockQuantity(3L);
        productLowStock.setMinimumQuantity(5L);

        when(repository.findProductsWithLowStock()).thenReturn(Collections.singletonList(productLowStock));

        boolean result = productService.checkStock();

        assertTrue(result, "Esperado que o resultado seja verdadeiro quando houver produtos com estoque baixo");
    }
}