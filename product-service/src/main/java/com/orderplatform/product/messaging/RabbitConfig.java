package com.orderplatform.product.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HAFTA 4: product-service, order-service'in yayınladığı olayları dinler.
 *
 *  order-service --(order.created)--> [order.events exchange] --> [product.order-created queue] --> product-service
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "order.events";
    public static final String ORDER_CREATED_QUEUE = "product.order-created";
    public static final String ORDER_CREATED_KEY = "order.created";

    @Bean
    TopicExchange orderEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE).build();
    }

    @Bean
    Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderEventsExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderEventsExchange).with(ORDER_CREATED_KEY);
    }

    /**
     * JSON mesaj. Gönderen tarafın sınıf adı (__TypeId__ header) bu serviste yok,
     * bu yüzden tipi @RabbitListener metodunun parametresinden çıkarıyoruz (INFERRED).
     */
    @Bean
    MessageConverter jsonMessageConverter() {
        var typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        var converter = new Jackson2JsonMessageConverter();
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
