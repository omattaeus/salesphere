package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.expirationDate <= :date")
    List<Product> findProductsExpiringSoon(@Param("date") LocalDate date);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < p.minimumQuantity")
    List<Product> findProductsWithLowStock();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :minimumQuantity")
    List<Product> findByStockQuantityLessThan(Long minimumQuantity);

    boolean existsByCodeSku(String codeSku);
}