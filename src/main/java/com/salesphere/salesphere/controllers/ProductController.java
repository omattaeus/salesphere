package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping(value = "/products")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseDTO> handleGetAllProducts() {
        List<ProductResponseDTO> productList = productService.getAllProducts();
        return productList;
    }

    @GetMapping("/low-stock")
    public List<Product> getProductsWithLowStock() {
        List<Product> lowStockProducts = productService.getProductsWithLowStock();
        if (lowStockProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Nenhum produto com estoque baixo.");
        }
        return lowStockProducts;
    }

    @GetMapping("/check-stock")
    public ResponseEntity<String> checkStock() {
        boolean hasLowStock = productService.checkStock();
        if (!hasLowStock) {
            return ResponseEntity.ok("Nenhum produto com estoque baixo.");
        } else {
            return ResponseEntity.ok("Alerta de estoque baixo enviado por e-mail.");
        }
    }

    @PostMapping
    public ProductResponseDTO handleCreateProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponse = productService.createProduct(productRequestDTO);
        return productResponse;
    }
}