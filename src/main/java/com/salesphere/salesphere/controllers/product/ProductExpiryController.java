package com.salesphere.salesphere.controllers.product;

import com.salesphere.salesphere.services.product.inventory.ProductExpiryService;
import com.salesphere.salesphere.exceptions.ProductExpiryException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/expiry")
public class ProductExpiryController {

    private final ProductExpiryService productExpiryService;

    public ProductExpiryController(ProductExpiryService productExpiryService) {
        this.productExpiryService = productExpiryService;
    }

    @PostMapping("/check")
    public ResponseEntity<String> checkExpiryDates() {
        try {
            productExpiryService.checkExpiryDates();
            return ResponseEntity.ok("Datas de validade verificadas com sucesso.");
        } catch (Exception e) {
            throw new ProductExpiryException("Erro ao verificar datas de validade: " + e.getMessage());
        }
    }
}