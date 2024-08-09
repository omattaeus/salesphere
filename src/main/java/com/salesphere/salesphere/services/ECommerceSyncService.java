package com.salesphere.salesphere.services;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ECommerceSyncService implements ExternalSystemSyncService {

    private final ExternalECommerceApiClient apiClient;
    private final ProductRepository productRepository;

    public ECommerceSyncService(ExternalECommerceApiClient apiClient, ProductRepository productRepository) {
        this.apiClient = apiClient;
        this.productRepository = productRepository;
    }

    @Override
    public void syncStockData() {
        List<Product> products = productRepository.findAll();
        apiClient.updateStock(products);
    }
}