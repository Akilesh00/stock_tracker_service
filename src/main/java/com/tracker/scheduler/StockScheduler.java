package com.tracker.scheduler;

import com.tracker.config.StockState;
import com.tracker.service.EmailService;
import com.tracker.service.SeleniumStockCheckerService;
import com.tracker.service.StockCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockScheduler {

    private final SeleniumStockCheckerService stockService;

    public StockScheduler(SeleniumStockCheckerService stockService) {
        this.stockService = stockService;
    }

    @Scheduled(fixedRate = 300000) // 5 min
    public void checkStock() {
        System.out.println("Checking stock...");
        stockService.isInStock();
    }
}