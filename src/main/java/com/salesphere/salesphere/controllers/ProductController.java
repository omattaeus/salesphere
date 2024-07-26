package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.services.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/product")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/get")
    public List<ProductResponseDTO> getProduct() {
        return productService.getProducts();
    }

    @PostMapping(value = "/create")
    public ProductResponseDTO createProduct(@RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO create = productService.createProduct(productRequestDTO);
        return create;
    }

    @GetMapping("/low-stock")
    public List<Product> getProductsWithLowStock() {
        return productService.getProductsWithLowStock();
    }

    @GetMapping("/check-stock")
    public void checkStock() {
        productService.checkStock();
    }
}