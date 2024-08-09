package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.WarehouseProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, Long> {

    @Query("SELECT wp FROM WarehouseProduct wp WHERE wp.warehouse.id = :warehouseId AND wp.product.id = :productId")
    Optional<WarehouseProduct> findByWarehouseAndProduct(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);

    @Query("DELETE FROM WarehouseProduct wp WHERE wp.warehouse.id = :warehouseId AND wp.product.id = :productId")
    void deleteByWarehouseAndProduct(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);
}