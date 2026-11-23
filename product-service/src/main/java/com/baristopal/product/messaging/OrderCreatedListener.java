package com.baristopal.product.messaging;

import com.baristopal.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final ProductService productService;

    public OrderCreatedListener(ProductService productService) {
        this.productService = productService;
    }

    @RabbitListener(queues = RabbitConfig.ORDER_CREATED_QUEUE)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("OrderCreated alındı: {}", event);
        productService.decreaseStock(event.productId(), event.quantity());
    }
}
