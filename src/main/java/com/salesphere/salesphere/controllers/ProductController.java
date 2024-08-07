package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.services.ProductService;
import com.salesphere.salesphere.services.email.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping(value = "/products")
@RestController
public class ProductController {

    private final ProductService productService;
    private final EmailService emailService;

    public ProductController(ProductService productService, EmailService emailService) {
        this.productService = productService;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getProductsWithLowStock() {
        List<ProductResponseDTO> lowStockProducts = productService.getProductsWithLowStock();
        if (lowStockProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(lowStockProducts);
    }

    @GetMapping("/check-stock")
    public ResponseEntity<String> checkStock() {
        List<Product> lowStockProducts = productService.getRawProductsWithLowStock();
        if (lowStockProducts.isEmpty()) {
            return ResponseEntity.ok("Nenhum produto com estoque baixo.");
        } else {
            emailService.sendLowStockAlert(lowStockProducts);
            return ResponseEntity.ok("Alerta de estoque baixo enviado por e-mail.");
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponse = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<List<ProductResponseDTO>> createProducts(@RequestBody List<ProductRequestDTO> productRequestDTOs) {
        List<ProductResponseDTO> productResponses = productService.createProducts(productRequestDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponses);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable("productId") Long productId, @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> partialUpdateProduct(@PathVariable("productId") Long productId, @RequestBody Map<String, Object> updates) {
        ProductResponseDTO updatedProduct = productService.partialUpdateProduct(productId, updates);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).build();
        }
    }
}