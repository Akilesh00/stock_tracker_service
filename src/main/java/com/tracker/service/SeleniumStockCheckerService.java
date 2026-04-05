package com.tracker.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class SeleniumStockCheckerService {

    private final WebDriver driver;

    private static final String PRODUCT_URL =
            "https://shop.amul.com/en/product/amul-high-protein-rose-lassi-200-ml-or-pack-of-30";

    public SeleniumStockCheckerService(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isInStock() {
        try {
            driver.get(PRODUCT_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait until page fully loads
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            System.out.println("Page loaded, checking stock...");

            // 🔥 Try to find "Sold Out" banner
            List<WebElement> soldOutElements = driver.findElements(
                    By.xpath("//*[contains(text(),'Sold Out') or contains(text(),'SOLD OUT')]")
            );

            if (!soldOutElements.isEmpty()) {
                System.out.println("OUT OF STOCK");
                return false;
            }

            // 🔥 Try to find "Add to Cart" button
            List<WebElement> addToCart = driver.findElements(
                    By.xpath("//button[contains(., 'Add to Cart')]")
            );

            if (!addToCart.isEmpty()) {
                System.out.println("IN STOCK");
                return true;
            }

            // 🔥 fallback: check inventory text
            String page = driver.getPageSource().toLowerCase();

            if (page.contains("out of stock") || page.contains("sold out")) {
                return false;
            }
            List<WebElement> allText = driver.findElements(By.xpath("//*"));

            for (WebElement el : allText) {
                String text = el.getText();
                if (text.toLowerCase().contains("stock") || text.toLowerCase().contains("sold")) {
                    System.out.println("FOUND TEXT: " + text);
                }
            }

            System.out.println("UNKNOWN STATE (fallback)");
            return false;



        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}