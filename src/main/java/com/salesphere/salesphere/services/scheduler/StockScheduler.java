package com.salesphere.salesphere.services.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockScheduler {

    private final StockCheckStrategy stockCheckStrategy;

    public StockScheduler(StockCheckStrategy stockCheckStrategy) {
        this.stockCheckStrategy = stockCheckStrategy;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkStockPeriodically() {
        stockCheckStrategy.checkStock();
    }
}