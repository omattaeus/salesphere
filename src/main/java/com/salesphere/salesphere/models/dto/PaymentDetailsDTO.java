package com.salesphere.salesphere.models.dto;

public record PaymentDetailsDTO(String paymentMethodId,
                                String cardNumber,
                                int expMonth,
                                int expYear,
                                String cvc) {}
