package com.salesphere.salesphere.repositories.product.warehouse;

import com.salesphere.salesphere.models.product.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("SELECT wp.quantity FROM WarehouseProduct wp WHERE wp.product.id = :productId AND wp.warehouse.id = :warehouseId")
    int getProductQuantityInWarehouse(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);

    @Modifying
    @Query("UPDATE WarehouseProduct wp SET wp.quantity = :quantity WHERE wp.product.id = :productId AND wp.warehouse.id = :warehouseId")
    void updateProductQuantityInWarehouse(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("quantity") int quantity);
}