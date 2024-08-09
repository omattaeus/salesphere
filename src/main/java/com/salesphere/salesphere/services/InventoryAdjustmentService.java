package com.salesphere.salesphere.services;

import com.salesphere.salesphere.models.InventoryMovement;
import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.repositories.InventoryMovementRepository;
import com.salesphere.salesphere.repositories.ProductRepository;
import com.salesphere.salesphere.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryAdjustmentService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    public InventoryAdjustmentService(ProductRepository productRepository, InventoryMovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public void adjustInventory(Long productId, int adjustmentQuantity, String reason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        product.setStockQuantity(product.getStockQuantity() + adjustmentQuantity);
        productRepository.save(product);

        InventoryMovement movement = new InventoryMovement(product, (long) adjustmentQuantity, reason);
        movementRepository.save(movement);
    }

}