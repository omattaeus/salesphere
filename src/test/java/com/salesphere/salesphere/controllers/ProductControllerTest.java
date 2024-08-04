package com.salesphere.salesphere.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.models.enums.AvailabilityEnum;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import com.salesphere.salesphere.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("You must return all products successfully")
    public void shouldReturnAllProducts() throws Exception {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                "Camiseta Puma", "Camiseta esportiva de algodão", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(productResponseDTO));

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Camiseta Puma"))
                .andExpect(jsonPath("$[0].description").value("Camiseta esportiva de algodão"));
    }

    @Test
    @DisplayName("Must return low stock products successfully")
    public void shouldReturnProductsWithLowStock() throws Exception {
        Product productLowStock = new Product();
        productLowStock.setStockQuantity(3L);
        productLowStock.setMinimumQuantity(5L);

        Product productNormalStock = new Product();
        productNormalStock.setStockQuantity(10L);
        productNormalStock.setMinimumQuantity(5L);

        when(productService.getProductsWithLowStock()).thenReturn(Arrays.asList(productLowStock));

        mockMvc.perform(get("/products/low-stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").value(3L));
    }

    @Test
    @DisplayName("Must perform stock check successfully")
    public void shouldCheckStock() throws Exception {
        doNothing().when(productService).checkStock();

        mockMvc.perform(get("/products/check-stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).checkStock();
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso")
    public void shouldCreateProduct() throws Exception {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "Camiseta Puma", "Camiseta esportiva de algodão", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                "Camiseta Puma", "Camiseta esportiva de algodão", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        when(productService.createProduct(productRequestDTO)).thenReturn(productResponseDTO);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Camiseta Puma"))
                .andExpect(jsonPath("$.description").value("Camiseta esportiva de algodão"));
    }

    @Test
    @DisplayName("It should return an error when trying to create an invalid product")
    public void shouldReturnErrorForInvalidProduct() throws Exception {
        ProductRequestDTO invalidProductRequestDTO = new ProductRequestDTO(
                "", "", "", // Valores inválidos
                null, 0.00, 0.00, 0L, 0L,
                "", null
        );

        when(productService.createProduct(invalidProductRequestDTO))
                .thenThrow(new RuntimeException("Product cannot be empty!"));

        MvcResult result = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidProductRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("errors"))
                .andExpect(jsonPath("$.message").value("Product cannot be empty!"))
                .andReturn();
    }
}