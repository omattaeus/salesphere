package com.salesphere.salesphere.controllers.inventory;

import com.salesphere.salesphere.models.inventory.InventoryMovement;
import com.salesphere.salesphere.services.product.inventory.InventoryMovementService;
import com.salesphere.salesphere.exceptions.InventoryMovementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory-movements")
public class InventoryMovementController {

    private final InventoryMovementService inventoryMovementService;

    public InventoryMovementController(InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryMovement>> getMovementsForProduct(@PathVariable Long productId) {
        try {
            List<InventoryMovement> movements = inventoryMovementService.getMovementsForProduct(productId);
            return ResponseEntity.ok(movements);
        } catch (Exception e) {
            throw new InventoryMovementException("Erro ao recuperar movimentos para o produto: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> recordMovement(@RequestBody InventoryMovement movement) {
        try {
            inventoryMovementService.recordMovement(movement);
            return ResponseEntity.ok("Movimento registrado com sucesso.");
        } catch (Exception e) {
            throw new InventoryMovementException("Erro ao registrar movimento: " + e.getMessage());
        }
    }
}