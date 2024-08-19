package com.salesphere.salesphere.controllers.shopify;

import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.services.shopify.ShopifyApiClient;
import com.salesphere.salesphere.exceptions.ExternalApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/external-ecommerce")
public class ShopifyApiController {

    private final ShopifyApiClient shopifyApiClient;

    public ShopifyApiController(ShopifyApiClient shopifyApiClient) {
        this.shopifyApiClient = shopifyApiClient;
    }

    @PostMapping("/update-stock")
    public ResponseEntity<String> updateStock(@RequestBody List<Product> products) {
        try {
            shopifyApiClient.updateStock(products);
            return ResponseEntity.ok("Estoque atualizado com sucesso.");
        } catch (ExternalApiException e) {
            throw new ExternalApiException("Erro ao atualizar estoque: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalApiException("Erro desconhecido ao atualizar estoque.");
        }
    }
}