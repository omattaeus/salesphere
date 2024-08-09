package com.salesphere.salesphere.services.discount;

public interface DiscountPolicy {
    double apply(double price);
}