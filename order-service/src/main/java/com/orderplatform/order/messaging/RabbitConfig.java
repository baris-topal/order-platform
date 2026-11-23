package com.orderplatform.order.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** order-service sadece exchange'e yayın yapar; kuyrukları dinleyen servisler tanımlar. */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "order.events";
    public static final String ORDER_CREATED_KEY = "order.created";

    @Bean
    TopicExchange orderEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
