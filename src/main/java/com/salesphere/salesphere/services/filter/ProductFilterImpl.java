package com.salesphere.salesphere.services.filter;

import com.salesphere.salesphere.models.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductFilterImpl implements ProductFilter {

    @Override
    public List<Product> filter(List<Product> products, String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return products;
        }
        return products.stream()
                .filter(product -> product.getProductName().toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());
    }
}