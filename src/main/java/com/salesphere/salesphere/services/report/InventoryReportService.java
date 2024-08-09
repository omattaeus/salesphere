package com.salesphere.salesphere.services.report;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryReportService {

    private final ProductRepository productRepository;

    public InventoryReportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public InventoryReport generateLowStockReport() {
        List<Product> lowStockProducts = productRepository.findProductsWithLowStock();
        return new InventoryReport(lowStockProducts);
    }
}