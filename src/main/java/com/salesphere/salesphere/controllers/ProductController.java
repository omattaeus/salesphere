package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.services.ProductService;
import org.springframework.web.bind.annotation.*;

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
        return productService.getProductsWithLowStock();
    }

    @GetMapping("/check-stock")
    public void checkStock() {
        productService.checkStock();
    }

    @PostMapping
    public ProductResponseDTO handleCreateProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponse = productService.createProduct(productRequestDTO);
        return productResponse;
    }
}