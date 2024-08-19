package com.salesphere.salesphere.services.scheduler;

import com.salesphere.salesphere.models.product.Product;
import com.salesphere.salesphere.repositories.product.ProductRepository;
import com.salesphere.salesphere.services.email.EmailService;
import com.salesphere.salesphere.services.websocket.StockWebSocketHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplenishmentService implements StockReplenishmentStrategy {

    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final StockWebSocketHandler webSocketHandler;

    public ReplenishmentService(ProductRepository productRepository, EmailService emailService,
                                StockWebSocketHandler webSocketHandler) {
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void checkAndReplenishStock() {
        List<Product> lowStockProducts = productRepository.findProductsWithLowStock();

        if (!lowStockProducts.isEmpty()) {
            String message = createReplenishmentMessage(lowStockProducts);
            notifyReplenishment(message);
        }
    }

    private String createReplenishmentMessage(List<Product> products) {
        return products.stream()
                .map(p -> String.format("%s: %d unidades", p.getProductName(),
                        p.getStockQuantity()))
                .collect(Collectors.joining("\n"));
    }

    private void notifyReplenishment(String message) {
        webSocketHandler.broadcastMessage(message);
        emailService.sendStockReplenishmentAlert(message);
    }
}