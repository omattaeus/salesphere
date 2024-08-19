package com.salesphere.salesphere.models.dto.payment;

public record PaymentDetailsDTO(String paymentMethodId,
                                String cardNumber,
                                int expMonth,
                                int expYear,
                                String cvc) {}
