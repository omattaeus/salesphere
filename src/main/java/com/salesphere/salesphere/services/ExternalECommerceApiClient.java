package com.salesphere.salesphere.services;

import com.salesphere.salesphere.models.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class ExternalECommerceApiClient {

    private final WebClient webClient;

    @Value("${shopify.api.base-url}")
    private String baseUrl;

    @Value("${shopify.api.access-token}")
    private String accessToken;

    public ExternalECommerceApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("X-Shopify-Access-Token", accessToken)
                .build();
    }

    public void updateStock(List<Product> products) {
        for (Product product : products) {
            try {
                webClient.post()
                        .uri("/admin/api/2024-07/inventory_levels/set.json")
                        .bodyValue(createStockUpdatePayload(product))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();

                System.out.println("Atualizado estoque do produto: " + product.getProductName());
            } catch (WebClientResponseException e) {
                System.err.println("Erro ao atualizar estoque para o produto " + product.getProductName() + ": " + e.getMessage());
            }
        }
    }

    private StockUpdatePayload createStockUpdatePayload(Product product) {
        return new StockUpdatePayload(product.getInventoryItemId(), product.getStockQuantity());
    }
}