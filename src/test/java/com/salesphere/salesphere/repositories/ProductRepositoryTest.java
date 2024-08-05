package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@SpringJUnitConfig
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup() {
        Category category = new Category();
        category.setCategoryEnum(CategoryEnum.MALE);
        categoryRepository.save(category);

        Product product1 = new Product(
                null, "Product1", "Description1", "Brand1",
                100.00, 150.00, 5L, 10L,  // Ordens e tipos corretos
                "SKU001", category,  // Mova category para a posição correta
                AvailabilityEnum.AVAILABLE
        );

        Product product2 = new Product(
                null, "Product2", "Description2", "Brand2",
                200.00, 250.00, 15L, 20L,
                "SKU002", category,
                AvailabilityEnum.OUT_OF_STOCK
        );

        Product product3 = new Product(
                null, "Product3", "Description3", "Brand3",
                300.00, 350.00, 25L, 20L,
                "SKU003", category,
                AvailabilityEnum.AVAILABLE
        );

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        List<Product> allProducts = productRepository.findAll();
        assertEquals(3, allProducts.size(), "There should be 3 products in the database");
    }

    @Test
    @DisplayName("Should find products with stock quantity less than the minimum quantity")
    public void testFindProductsWithLowStock() {
        List<Product> result = productRepository.findProductsWithLowStock();

        System.out.println("Result size: " + result.size());
        for (Product product : result) {
            System.out.println("Product: " + product.getProductName() + ", Stock: " + product.getStockQuantity());
        }

        assertEquals(2, result.size(), "There should be 2 products with stock quantity less than their minimum quantity");
        assertTrue(result.stream().anyMatch(product -> product.getProductName().equals("Product1")), "Product1 should be present in the result");
        assertTrue(result.stream().anyMatch(product -> product.getProductName().equals("Product2")), "Product2 should be present in the result");
    }

    @Test
    @DisplayName("Should return an empty list if no products have stock quantity less than the given value")
    public void testFindByStockQuantityLessThanNoProducts() {
        Long minimumQuantity = 1L;

        List<Product> result = productRepository.findByStockQuantityLessThan(minimumQuantity);

        System.out.println("Result size: " + result.size());

        assertTrue(result.isEmpty(), "The result should be empty if no products have stock quantity less than 1");
    }
}