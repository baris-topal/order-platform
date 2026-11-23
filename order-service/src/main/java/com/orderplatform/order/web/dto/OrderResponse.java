package com.orderplatform.order.web.dto;

import com.orderplatform.order.domain.Order;
import com.orderplatform.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(Long id, Long productId, int quantity, BigDecimal totalPrice,
                            OrderStatus status, Instant createdAt) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getProductId(), order.getQuantity(),
                order.getTotalPrice(), order.getStatus(), order.getCreatedAt());
    }
}
