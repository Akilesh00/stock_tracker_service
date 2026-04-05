package com.tracker.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class StockCheckerService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StockCheckerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean isInStock() {
        try {
            String query = URLEncoder.encode(
                    "{\"alias\":\"amul-high-protein-rose-lassi-200-ml-or-pack-of-30\"}",
                    StandardCharsets.UTF_8
            );

            //String jsessionid="s%3AsMRAUOj12cv5pxbJm%2F1FewAL.IQUgy5KAsYXCAdyWjvkiwb2Y15D18%2BTwiT%2FoLgPvpPE; _fbp=fb.1.1775328702229.985436447742552535; _ga=GA1.1.380805005.1775328703; _gcl_au=1.1.1614770887.1775328703.1446055678.1775328727.1775328727; _ga_E69VZ8HPCN=GS2.1.s1775328702$o1$g1$t1775328744$j18$l0$h1796228912; __cf_bm=g4QmquY56J.ARzbxfBhQ7jxWvOqWKbrM3XX_F9VC0go-1775328744.8417704-1.0.1.1-Tb2D2M6kOCkcp6k8Hrb09y8nHSARgfwGM8IR7Mbx5nbhuYDZwGRBGJN8Qq0u6uyqV.0Rk6_SG3I8Uy17CfBurslmBDZBjkvomOWQr0m1BW16mzmvCWXRCWJilyh9KFly; _cfuvid=sPYj_x3XAsCmM5nQ3wETKOXgCjBbfGr33S0vlf8GGxo-1775328744.8417704-1.0.1.1-jCG7d_7md3KUDROibe6JujXUzoLXlprcmpil8jgr1Tw";
            String cookies = "jsessionid=s%3AsMRAUOj12cv5pxbJm%2F1FewAL.IQUgy5KAsYXCAdyWjvkiwb2Y15D18%2BTwiT%2FoLgPvpPE; " +
                    "_fbp=fb.1.1775328702229.985436447742552535; " +
                    "_ga=GA1.1.380805005.1775328703; " +
                    "_gcl_au=1.1.1614770887.1775328703.1446055678.1775328727.1775328727; " +
                    "_cfuvid=sPYj_x3XAsCmM5nQ3wETKOXgCjBbfGr33S0vlf8GGxo-1775328744.8417704-1.0.1.1-jCG7d_7md3KUDROibe6JujXUzoLXlprcmpil8jgr1Tw; " +
                    "__cf_bm=5GxNFbA9nyf6QdM2jOWM_GXTXcCaXy2ErwuqOZklrMY-1775329656.5592942-1.0.1.1-wJ9J_LdGJAYUifUog6eP3cQHQxdqhafJ1XmnXn.QnxaxweeT_3zFCwC0l0JGt07kDi1VSRzE3LvSIJiN0R.Vjtfcqa37QE7clQjx4W7LXwO0mVDlGblQhGVIW6VtJyWp; " +
                    "_ga_E69VZ8HPCN=GS2.1.s1775328702$o1$g1$t1775329656$j60$l0$h1796228912";

            String uri = "/api/1/entity/ms.products?q=" + query + "&limit=1";

            String response = webClient.get()
                    .uri(uri)
                    .header("referer", "https://shop.amul.com/en/product/amul-high-protein-rose-lassi-200-ml-or-pack-of-30")
                    .header("frontend", "1")
                    .header("origin", "https://shop.amul.com")
                    .header("accept-language", "en-US,en;q=0.9")
                    .header("cookie", cookies)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> {
                        System.out.println("HTTP ERROR: " + res.statusCode());
                        return res.bodyToMono(String.class)
                                .map(body -> new RuntimeException(body));
                    })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .doOnError(err -> System.out.println("ERROR: " + err.getMessage()))
                    .block();

            JsonNode product = objectMapper.readTree(response).path("data").get(0);

            return product.path("available").asBoolean()
                    && product.path("inventory_quantity").asInt() > 0;

        } catch (Exception e) {
            return false;
        }
    }
}

//public boolean isInStock() {
//    try {
//        String query = URLEncoder.encode(
//                "{\"alias\":\"amul-high-protein-rose-lassi-200-ml-or-pack-of-30\"}",
//                StandardCharsets.UTF_8
//        );
//
//        String url = "https://shop.amul.com/api/1/entity/ms.products?q=" + query + "&limit=1";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("User-Agent", "Mozilla/5.0");
//        headers.set("Accept", "application/json");
//
//        // 🔥 REQUIRED (this fixes 401)
//        headers.set("referer", "https://shop.amul.com/en/product/amul-high-protein-rose-lassi-200-ml-or-pack-of-30");
//        headers.set("frontend", "1");
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                String.class
//        );
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(response.getBody());
//
//        JsonNode product = root.path("data").get(0);
//
//        boolean available = product.path("available").asBoolean();
//        int quantity = product.path("inventory_quantity").asInt();
//
//        System.out.println("Available: " + available);
//        System.out.println("Quantity: " + quantity);
//
//        return available && quantity > 0;
//
//    } catch (Exception e) {
//        System.out.println("Error: " + e.getMessage());
//        return false;
//    }
//}