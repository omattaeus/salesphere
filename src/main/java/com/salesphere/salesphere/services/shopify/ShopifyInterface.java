package com.salesphere.salesphere.services.shopify;

import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.repositories.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopifyInterface implements ShopifySystemInterface {

    private final ShopifyApiClient apiClient;
    private final ProductRepository productRepository;

    public ShopifyInterface(ShopifyApiClient apiClient, ProductRepository productRepository) {
        this.apiClient = apiClient;
        this.productRepository = productRepository;
    }

    @Override
    public void syncStockData() {
        List<Product> products = productRepository.findAll();
        apiClient.updateStock(products);
    }
}