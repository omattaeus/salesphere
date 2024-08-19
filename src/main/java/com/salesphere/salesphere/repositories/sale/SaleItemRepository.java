package com.salesphere.salesphere.repositories.sale;

import com.salesphere.salesphere.models.sale.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
}