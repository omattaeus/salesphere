package com.salesphere.salesphere.services.product.inventory;

import com.salesphere.salesphere.models.inventory.InventoryMovement;
import com.salesphere.salesphere.repositories.inventory.InventoryMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryMovementService {

    private final InventoryMovementRepository movementRepository;

    public InventoryMovementService(InventoryMovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    public void recordMovement(InventoryMovement movement) {
        movementRepository.save(movement);
    }

    public List<InventoryMovement> getMovementsForProduct(Long productId) {
        return movementRepository.findByProductId(productId);
    }
}