package com.salesphere.salesphere.controllers.shopify;

import com.salesphere.salesphere.services.shopify.ShopifyInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final ShopifyInterface shopifySyncService;

    public SyncController(ShopifyInterface shopifySyncService) {
        this.shopifySyncService = shopifySyncService;
    }

    @PostMapping("/stock")
    public ResponseEntity<String> syncStock() {
        try {
            shopifySyncService.syncStockData();
            return ResponseEntity.ok("Sincronização de estoque concluída com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao sincronizar estoque: " + e.getMessage());
        }
    }
}