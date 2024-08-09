package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.services.ExternalECommerceApiClient;
import com.salesphere.salesphere.exceptions.ExternalApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/external-ecommerce")
public class ExternalECommerceApiController {

    private final ExternalECommerceApiClient externalECommerceApiClient;

    public ExternalECommerceApiController(ExternalECommerceApiClient externalECommerceApiClient) {
        this.externalECommerceApiClient = externalECommerceApiClient;
    }

    @PostMapping("/update-stock")
    public ResponseEntity<String> updateStock(@RequestBody List<Product> products) {
        try {
            externalECommerceApiClient.updateStock(products);
            return ResponseEntity.ok("Estoque atualizado com sucesso.");
        } catch (ExternalApiException e) {
            throw new ExternalApiException("Erro ao atualizar estoque: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalApiException("Erro desconhecido ao atualizar estoque.");
        }
    }
}