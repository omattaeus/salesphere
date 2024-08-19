package com.salesphere.salesphere.services.payment;

import com.salesphere.salesphere.models.sale.Sale;
import com.salesphere.salesphere.exceptions.PaymentProcessingException;
import com.salesphere.salesphere.models.dto.payment.PaymentDetailsDTO;
import com.stripe.Stripe;
import com.stripe.exception.ApiException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.AuthenticationException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${stripe.api.secretKey}")
    private String stripeSecretKey;

    public PaymentService() {
        Stripe.apiKey = stripeSecretKey;
    }

    public boolean processPayment(Sale sale, PaymentDetailsDTO paymentDetails) {
        try {
            long amount = (long) (sale.getTotalAmount() * 100);

            PaymentIntentCreateParams paymentIntentParams = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency("brl")
                    .setPaymentMethod(paymentDetails.paymentMethodId())
                    .setConfirm(true)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentParams);

            return "succeeded".equals(paymentIntent.getStatus());
        } catch (AuthenticationException e) {
            logger.error("Erro de autenticação ao processar pagamento: {}", e.getMessage());
            throw new PaymentProcessingException("Erro de autenticação. Por favor, tente novamente mais tarde.");
        } catch (InvalidRequestException e) {
            logger.error("Erro de solicitação inválida ao processar pagamento: {}", e.getMessage());
            throw new PaymentProcessingException("Solicitação de pagamento inválida. Verifique os detalhes e tente novamente.");
        } catch (ApiException e) {
            logger.error("Erro de API ao processar pagamento: {}", e.getMessage());
            throw new PaymentProcessingException("Ocorreu um erro com o serviço de pagamento. Por favor, tente novamente mais tarde.");
        } catch (CardException e) {
            logger.error("Erro com o cartão ao processar pagamento: {}", e.getMessage());
            throw new PaymentProcessingException("Erro com o cartão. Verifique os detalhes do cartão e tente novamente.");
        } catch (Exception e) {
            logger.error("Erro inesperado ao processar pagamento: {}", e.getMessage());
            throw new PaymentProcessingException("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        }
    }
}