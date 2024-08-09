package com.salesphere.salesphere.services;

import com.salesphere.salesphere.models.Product;
import com.salesphere.salesphere.models.enums.StatusEnum;
import com.salesphere.salesphere.repositories.ProductRepository;
import com.salesphere.salesphere.services.email.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductExpiryService {

    private final ProductRepository productRepository;
    private final EmailService emailService;

    public ProductExpiryService(ProductRepository productRepository, EmailService emailService) {
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiryDates() {
        LocalDate today = LocalDate.now();
        List<Product> expiringProducts = productRepository.findProductsExpiringSoon(today);

        if (!expiringProducts.isEmpty()) {
            emailService.sendLowStockAlert(expiringProducts);

            for (Product product : expiringProducts) {
                product.setStatus(StatusEnum.EXPIRING_SOON);
                productRepository.save(product);
            }
        }
    }
}