package com.salesphere.salesphere.repositories.inventory;

import com.salesphere.salesphere.models.inventory.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByProductId(Long productId);
}