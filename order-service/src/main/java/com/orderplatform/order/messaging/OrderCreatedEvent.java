package com.orderplatform.order.messaging;

public record OrderCreatedEvent(Long orderId, Long productId, int quantity) {
}
