package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.models.dto.ProductRequestDTO;
import com.salesphere.salesphere.models.dto.ProductResponseDTO;
import com.salesphere.salesphere.services.ProductService;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/product")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/get")
    public void getProduct(){
        productService.getProducts();
    }

    @PostMapping(value = "/create")
    public ProductResponseDTO createProduct(@RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO create = productService.createProduct(productRequestDTO);
        return create;
    }
}
