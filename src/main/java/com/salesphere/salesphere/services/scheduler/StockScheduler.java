package com.salesphere.salesphere.services.scheduler;

import com.salesphere.salesphere.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockScheduler {

    @Autowired
    private ProductService productService;

    @Scheduled(cron = "0 0 * * * *")
    public void checkStockPeriodically() {
        productService.checkStock();
    }
}