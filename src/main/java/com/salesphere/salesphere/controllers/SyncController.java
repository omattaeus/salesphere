package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.services.ECommerceSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final ECommerceSyncService eCommerceSyncService;

    public SyncController(ECommerceSyncService eCommerceSyncService) {
        this.eCommerceSyncService = eCommerceSyncService;
    }

    @PostMapping("/stock")
    public ResponseEntity<String> syncStock() {
        try {
            eCommerceSyncService.syncStockData();
            return ResponseEntity.ok("Sincronização de estoque concluída com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao sincronizar estoque: " + e.getMessage());
        }
    }
}