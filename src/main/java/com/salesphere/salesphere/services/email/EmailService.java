package com.salesphere.salesphere.services.email;

import com.salesphere.salesphere.models.product.Product;

import java.util.List;

public interface EmailService {
    void sendLowStockAlert(List<Product> products);
    void sendStockReplenishmentAlert(String message);
}