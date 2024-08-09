package com.salesphere.salesphere.services.discount;

public class PercentageDiscountPolicy implements DiscountPolicy {

    private final double discountPercentage;

    public PercentageDiscountPolicy(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public double apply(double price) {
        return price - (price * discountPercentage / 100);
    }
}