package com.salesphere.salesphere.controllers.sale;

import com.salesphere.salesphere.models.sale.Sale;
import com.salesphere.salesphere.models.dto.PaymentDetailsDTO;
import com.salesphere.salesphere.models.dto.ProductSaleDTO;
import com.salesphere.salesphere.services.payment.PaymentService;
import com.salesphere.salesphere.services.payment.sale.SaleService;
import com.salesphere.salesphere.services.discount.DiscountPolicy;
import com.salesphere.salesphere.services.discount.PercentageDiscountPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;
    private final PaymentService paymentService;

    public SaleController(SaleService saleService, PaymentService paymentService) {
        this.saleService = saleService;
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSaleAndProcessPayment(@RequestBody SaleRequest request) {
        if (request.getSaleItems() == null || request.getSaleItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("A lista de itens de venda não pode estar vazia.", false));
        }

        try {
            DiscountPolicy discountPolicy = new PercentageDiscountPolicy(10);

            Sale sale = saleService.createSale(request.getSaleItems(), discountPolicy);

            boolean paymentSuccess = paymentService.processPayment(sale, request.getPaymentDetails());

            if (paymentSuccess) {
                return ResponseEntity.ok(new ApiResponse("Venda criada e pagamento processado com sucesso.", true));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Falha no processamento do pagamento.", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Erro ao processar a venda: " + e.getMessage(), false));
        }
    }

    public static class ApiResponse {
        private String message;
        private boolean success;

        public ApiResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long saleId) {
        try {
            Sale sale = saleService.getSaleById(saleId);
            return ResponseEntity.ok(sale);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public static class SaleRequest {
        private List<ProductSaleDTO> saleItems;
        private PaymentDetailsDTO paymentDetails;

        public List<ProductSaleDTO> getSaleItems() {
            return saleItems;
        }

        public void setSaleItems(List<ProductSaleDTO> saleItems) {
            this.saleItems = saleItems;
        }

        public PaymentDetailsDTO getPaymentDetails() {
            return paymentDetails;
        }

        public void setPaymentDetails(PaymentDetailsDTO paymentDetails) {
            this.paymentDetails = paymentDetails;
        }
    }
}