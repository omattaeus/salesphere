package com.salesphere.salesphere.services.scheduler;

import com.salesphere.salesphere.services.product.ProductService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockScheduler {

    private final ProductService productService;

    public StockScheduler(ProductService productService) {
        this.productService = productService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkStockPeriodically() {
        productService.checkStock();
    }
}
