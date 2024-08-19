package com.salesphere.salesphere.services.discount;

import com.salesphere.salesphere.models.product.Product;
import org.springframework.stereotype.Component;

@Component
public class DiscountService {

    public double applyDiscount(Product product, DiscountPolicy discountPolicy) {
        return discountPolicy.apply(product.getSalePrice());
    }
}