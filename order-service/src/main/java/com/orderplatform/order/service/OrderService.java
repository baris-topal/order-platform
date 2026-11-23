package com.orderplatform.order.service;

import com.orderplatform.order.client.ProductClient;
import com.orderplatform.order.client.ProductDto;
import com.orderplatform.order.domain.Order;
import com.orderplatform.order.domain.OrderRepository;
import com.orderplatform.order.exception.InsufficientStockException;
import com.orderplatform.order.exception.OrderNotFoundException;
import com.orderplatform.order.messaging.OrderCreatedEvent;
import com.orderplatform.order.messaging.OrderEventPublisher;
import com.orderplatform.order.web.dto.OrderRequest;
import com.orderplatform.order.web.dto.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Akış: ürünü REST ile sor -> stok kontrolü -> siparişi kaydet -> olayı yayınla.
     * Stok düşme işini product-service olayı alınca kendisi yapar.
     *
     * Mülakat notu: DB kaydı ile mesaj yayını aynı transaction'da değil. Kayıt başarılı olup
     * mesaj gitmezse (ya da tersi) tutarsızlık olur. Çözüm: Transactional Outbox pattern.
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        ProductDto product = productClient.getProduct(request.productId());

        if (product.stock() < request.quantity()) {
            throw new InsufficientStockException(product.id(), product.stock(), request.quantity());
        }

        BigDecimal total = product.price().multiply(BigDecimal.valueOf(request.quantity()));
        Order order = orderRepository.save(new Order(product.id(), request.quantity(), total));

        eventPublisher.publishOrderCreated(
                new OrderCreatedEvent(order.getId(), order.getProductId(), order.getQuantity()));

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
