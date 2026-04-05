//package com.tracker.service;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class StockScheduler {
//
//    private static final Logger log = LoggerFactory.getLogger(StockScheduler.class);
//
//    private final JavaMailSender mailSender;
//
//    @Value("${tracker.url}")
//    private String productUrl;
//
//    @Value("${tracker.recipient-email}")
//    private String recipientEmail;
//
//    @Value("${spring.mail.username}")
//    private String senderEmail;
//
//    // Tracks last known state — avoids repeat emails
//    private boolean wasInStock = false;
//
//    public StockScheduler(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    @Scheduled(fixedRate = 300_000) // every 5 minutes
//    public void checkStock() {
//        try {
//            log.info("Checking stock for: {}", productUrl);
//
//            Document doc = Jsoup.connect(productUrl)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
//                    .timeout(10_000)
//                    .get();
//
//            boolean inStock = isInStock(doc);
//            log.info("Stock status: {}", inStock ? "IN STOCK ✅" : "OUT OF STOCK ❌");
//
//            // Only alert on out-of-stock → in-stock transition
//            if (inStock && !wasInStock) {
//                sendAlert();
//            }
//
//            wasInStock = inStock;
//
//        } catch (Exception e) {
//            // Silent fail — just log, will retry next cycle
//            log.warn("Failed to check stock (will retry in 5 min): {}", e.getMessage());
//        }
//    }
//
//    /**
//     * Detects stock status from the page.
//     *
//     * Strategy: look for common "out of stock" signals.
//     * Flip the logic — if none found, assume in stock.
//     *
//     * Adjust selectors below based on actual page HTML.
//     */
//    private boolean isInStock(Document doc) {
//        String bodyText = doc.body().text().toLowerCase();
//
//        // Common out-of-stock signals — add more as needed
//        boolean outOfStock =
//                bodyText.contains("out of stock") ||
//                        bodyText.contains("sold out") ||
//                        bodyText.contains("currently unavailable") ||
//                        bodyText.contains("notify me") ||         // Amul uses this when OOS
//                        doc.select("button.notify-me").size() > 0 ||
//                        doc.select("[class*='out-of-stock']").size() > 0 ||
//                        doc.select("[class*='sold-out']").size() > 0;
//
//        // Also check for positive in-stock signals
//        boolean addToCartVisible =
//                doc.select("button.add-to-cart").size() > 0 ||
//                        doc.select("button[class*='add-to-cart']").size() > 0 ||
//                        bodyText.contains("add to cart");
//
//        // In stock = no OOS signals AND add-to-cart is visible
//        return !outOfStock && addToCartVisible;
//    }
//
//    private void sendAlert() {
//        try {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setFrom(senderEmail);
//            msg.setTo(recipientEmail);
//            msg.setSubject("✅ Back in Stock Alert!");
//            msg.setText(
//                    "Good news! The product you're tracking is back in stock.\n\n" +
//                            "👉 " + productUrl + "\n\n" +
//                            "Hurry before it sells out again!"
//            );
//            mailSender.send(msg);
//            log.info("Alert email sent to {}", recipientEmail);
//        } catch (Exception e) {
//            log.error("Failed to send email: {}", e.getMessage());
//        }
//    }
//}
//
