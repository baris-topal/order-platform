package com.orderplatform.order.client;

/**
 * HAFTA 4 - SENİN SIRAN: REST client testi.
 *
 * Gerçek product-service'e istek atmadan ProductClient'ı test etmek için:
 *   @RestClientTest(ProductClient.class)  + @Autowired MockRestServiceServer server
 *
 * Not: ProductClient constructor'ı ${product-service.url} istiyor.
 *   @RestClientTest(value = ProductClient.class, properties = "product-service.url=http://product")
 *
 * Yazılacak testler:
 *  1. 200 dönünce ProductDto doğru map ediliyor mu?
 *     server.expect(requestTo("http://product/api/products/1"))
 *           .andRespond(withSuccess("{...json...}", MediaType.APPLICATION_JSON));
 *  2. 404 dönünce ProductNotFoundException fırlıyor mu?  (withStatus(HttpStatus.NOT_FOUND))
 *  3. 500 dönünce ne oluyor? Davranışı test et ve GlobalExceptionHandler'da ele alınmalı mı düşün.
 */
class ProductClientTest {
}
