package com.salesphere.salesphere.services.discount;

public class FixedDiscountPolicy implements DiscountPolicy {

    private final double discountAmount;

    public FixedDiscountPolicy(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public double apply(double price) {
        return price - discountAmount;
    }
}