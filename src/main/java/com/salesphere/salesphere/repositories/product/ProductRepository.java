package com.salesphere.salesphere.repositories.product;

import com.salesphere.salesphere.models.product.Product;
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

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:name% AND p.stockQuantity >= :quantity")
    List<Product> findByProductNameAndStockQuantityGreaterThan(@Param("name") String name, @Param("quantity") Long quantity);

    List<Product> findByCodeSkuContainingIgnoreCase(String codeSku);

    boolean existsByCodeSku(String codeSku);
}