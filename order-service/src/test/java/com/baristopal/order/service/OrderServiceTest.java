package com.baristopal.order.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * HAFTA 1 - SENİN SIRAN.
 * Örnek: product-service/src/test/.../ProductServiceTest.java
 *
 * OrderService'in 3 bağımlılığı var: OrderRepository, ProductClient, OrderEventPublisher.
 * Üçünü de @Mock yap, OrderService'i @InjectMocks ile oluştur.
 *
 * Yazılacak testler:
 *  1. createOrder_success
 *     - productClient.getProduct(1L) -> stok 10, fiyat 100.00 olan ürün dönsün
 *     - quantity=3 ile çağır
 *     - totalPrice 300.00 mü? (BigDecimal karşılaştırmasında isEqualByComparingTo kullan, nedenini araştır)
 *     - eventPublisher.publishOrderCreated doğru productId/quantity ile çağrıldı mı? (ArgumentCaptor)
 *
 *  2. createOrder_insufficientStock
 *     - stok 2, quantity 5 -> InsufficientStockException
 *     - orderRepository.save ve eventPublisher HİÇ çağrılmamalı: verify(..., never())
 *
 *  3. createOrder_productNotFound
 *     - productClient ProductNotFoundException fırlatsın -> aynısı dışarı çıkmalı
 *
 *  4. getById_notFound
 *
 * Bonus:
 *  - verifyNoMoreInteractions ve InOrder ile "önce kaydet sonra yayınla" sırasını doğrula.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    // TODO: @Mock alanları, @InjectMocks OrderService ve testler
}
