package com.baristopal.order.client;

import com.baristopal.order.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * HAFTA 4: Senkron servis iletişimi (REST).
 * Sipariş oluşturmadan önce ürünün fiyatını ve stoğunu product-service'ten soruyoruz.
 */
@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient.Builder builder,
                         @Value("${product-service.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public ProductDto getProduct(Long productId) {
        return restClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new ProductNotFoundException(productId);
                })
                .body(ProductDto.class);
    }
}
