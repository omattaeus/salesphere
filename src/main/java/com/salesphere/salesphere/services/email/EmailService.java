package com.salesphere.salesphere.services.email;

import com.salesphere.salesphere.models.Product;

import java.util.List;

public interface EmailService {
    void sendLowStockAlert(List<Product> products);
}
