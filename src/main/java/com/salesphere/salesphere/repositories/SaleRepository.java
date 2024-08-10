package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}