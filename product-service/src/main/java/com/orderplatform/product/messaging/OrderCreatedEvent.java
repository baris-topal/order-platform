package com.orderplatform.product.messaging;

/** order-service'teki olayın bu servisteki karşılığı. Servisler kod paylaşmaz, sözleşme (JSON) paylaşır. */
public record OrderCreatedEvent(Long orderId, Long productId, int quantity) {
}
