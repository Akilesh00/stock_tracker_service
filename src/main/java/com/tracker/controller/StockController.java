package com.tracker.controller;

import com.tracker.service.StockCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockCheckerService stockCheckerService;

    @GetMapping("/check")
    public String checkNow() {
        return stockCheckerService.isInStock() ? "IN STOCK" : "OUT OF STOCK";
    }

    @GetMapping("/test")
    public String test() {
        return "Working!";
    }
}