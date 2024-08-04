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
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Should return all products successfully")
    public void shouldReturnAllProducts() throws Exception {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                "Puma T-Shirt", "Cotton sports t-shirt", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(productResponseDTO));

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Puma T-Shirt"))
                .andExpect(jsonPath("$[0].description").value("Cotton sports t-shirt"));
    }

    @Test
    @DisplayName("Should return low stock products successfully")
    public void shouldReturnProductsWithLowStock() throws Exception {
        Product productLowStock = new Product();
        productLowStock.setStockQuantity(3L);
        productLowStock.setMinimumQuantity(5L);

        when(productService.getProductsWithLowStock()).thenReturn(Collections.singletonList(productLowStock));

        mockMvc.perform(get("/products/low-stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockQuantity").value(3L));
    }

    @Test
    @DisplayName("Should check stock successfully")
    public void shouldCheckStock() throws Exception {
        when(productService.checkStock()).thenReturn(true);

        mockMvc.perform(get("/products/check-stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Alerta de estoque baixo enviado por e-mail."));
    }

    @Test
    @DisplayName("Should return no low stock products message")
    public void shouldReturnNoLowStockMessage() throws Exception {
        when(productService.checkStock()).thenReturn(false);

        mockMvc.perform(get("/products/check-stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Nenhum produto com estoque baixo."));
    }

    @Test
    @DisplayName("Should create a product successfully")
    public void shouldCreateProduct() throws Exception {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "Puma T-Shirt", "Cotton sports t-shirt", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                "Puma T-Shirt", "Cotton sports t-shirt", "Puma",
                CategoryEnum.MALE, 50.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        when(productService.createProduct(productRequestDTO)).thenReturn(productResponseDTO);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Puma T-Shirt"))
                .andExpect(jsonPath("$.description").value("Cotton sports t-shirt"));
    }

    @Test
    @DisplayName("Should return error when trying to create an invalid product")
    public void shouldReturnErrorForInvalidProduct() throws Exception {
        ProductRequestDTO invalidProductRequestDTO = new ProductRequestDTO(
                "", "", "",
                null, 0.00, 0.00, 0L, 0L,
                "", null
        );

        when(productService.createProduct(invalidProductRequestDTO))
                .thenThrow(new RuntimeException("Product cannot be empty!"));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidProductRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product cannot be empty!"));
    }

    @Test
    @DisplayName("Should update a product successfully")
    public void shouldUpdateProduct() throws Exception {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "Updated Puma T-Shirt", "Updated description", "Puma",
                CategoryEnum.MALE, 60.00, 90.00, 40L, 10L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        ProductResponseDTO updatedProductResponseDTO = new ProductResponseDTO(
                "Updated Puma T-Shirt", "Updated description", "Puma",
                CategoryEnum.MALE, 60.00, 90.00, 40L, 10L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        when(productService.updateProduct(anyLong(), eq(productRequestDTO))).thenReturn(updatedProductResponseDTO);

        mockMvc.perform(put("/products/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Updated Puma T-Shirt"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("Should partially update a product successfully")
    public void shouldPartiallyUpdateProduct() throws Exception {
        Map<String, Object> updates = Map.of("purchasePrice", 75.00, "description", "Partially updated description");

        ProductResponseDTO updatedProductResponseDTO = new ProductResponseDTO(
                "Puma T-Shirt", "Partially updated description", "Puma",
                CategoryEnum.MALE, 75.00, 80.00, 30L, 5L,
                "PUMA123", AvailabilityEnum.AVAILABLE
        );

        when(productService.partialUpdateProduct(anyLong(), eq(updates))).thenReturn(updatedProductResponseDTO);

        mockMvc.perform(patch("/products/{productId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchasePrice").value(75.00))
                .andExpect(jsonPath("$.description").value("Partially updated description"));
    }
}