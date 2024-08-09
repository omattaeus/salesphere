package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.models.InventoryMovement;
import com.salesphere.salesphere.services.InventoryAdjustmentService;
import com.salesphere.salesphere.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryAdjustmentController {

    private final InventoryAdjustmentService inventoryAdjustmentService;

    public InventoryAdjustmentController(InventoryAdjustmentService inventoryAdjustmentService) {
        this.inventoryAdjustmentService = inventoryAdjustmentService;
    }

    @PostMapping("/adjust")
    public ResponseEntity<String> adjustInventory(
            @RequestParam Long productId,
            @RequestParam int adjustmentQuantity,
            @RequestParam String reason) {
        try {
            inventoryAdjustmentService.adjustInventory(productId, adjustmentQuantity, reason);
            return ResponseEntity.ok("Inventário ajustado com sucesso.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao ajustar inventário: " + e.getMessage());
        }
    }
}