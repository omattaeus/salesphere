package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
}