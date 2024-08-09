package com.salesphere.salesphere.controllers;

import com.salesphere.salesphere.services.WarehouseInventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse-inventory")
public class WarehouseInventoryController {

    private final WarehouseInventoryService warehouseInventoryService;

    public WarehouseInventoryController(WarehouseInventoryService warehouseInventoryService) {
        this.warehouseInventoryService = warehouseInventoryService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferProduct(
            @RequestParam Long productId,
            @RequestParam Long fromWarehouseId,
            @RequestParam Long toWarehouseId,
            @RequestParam int quantity) {
        try {
            warehouseInventoryService.transferProduct(productId, fromWarehouseId, toWarehouseId, quantity);
            return new ResponseEntity<>("Produto transferido com sucesso", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}