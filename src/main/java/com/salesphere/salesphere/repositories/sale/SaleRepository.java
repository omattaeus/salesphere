package com.salesphere.salesphere.repositories.sale;

import com.salesphere.salesphere.models.sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}