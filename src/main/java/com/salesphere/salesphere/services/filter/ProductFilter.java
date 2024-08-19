package com.salesphere.salesphere.services.filter;

import com.salesphere.salesphere.models.product.Product;

import java.util.List;

public interface ProductFilter {
    List<Product> filter(List<Product> products, String searchQuery);
}