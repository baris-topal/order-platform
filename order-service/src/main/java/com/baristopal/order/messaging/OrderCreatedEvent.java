package com.baristopal.order.messaging;

public record OrderCreatedEvent(Long orderId, Long productId, int quantity) {
}
