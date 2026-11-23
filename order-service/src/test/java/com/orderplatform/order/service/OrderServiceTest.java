package com.orderplatform.order.service;

import com.orderplatform.order.client.ProductClient;
import com.orderplatform.order.client.ProductDto;
import com.orderplatform.order.domain.Order;
import com.orderplatform.order.domain.OrderRepository;
import com.orderplatform.order.exception.InsufficientStockException;
import com.orderplatform.order.exception.OrderNotFoundException;
import com.orderplatform.order.exception.ProductNotFoundException;
import com.orderplatform.order.messaging.OrderCreatedEvent;
import com.orderplatform.order.messaging.OrderEventPublisher;
import com.orderplatform.order.web.dto.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderEventPublisher orderEventPublisher;
    @Mock
    ProductClient productClient;
    @InjectMocks
    OrderService orderService;

    @Test
    @DisplayName("create: createOrder_success")
    void createOrder_success() {
        // given
        var product = new ProductDto(1L, "Ürün", new BigDecimal("100.00"), 10); // kendi DTO/Entity yapına göre
        when(productClient.getProduct(1L)).thenReturn(product);
        var request = new OrderRequest(product.id(), 3);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        var response = orderService.createOrder(request);
        // then
        // Sıra: ürünü sor -> siparişi kaydet -> olayı yayınla.
        // Kaydetmeden yayınlarsak tüketici DB'de olmayan bir siparişin olayını alır.
        InOrder inOrder = inOrder(productClient, orderRepository, orderEventPublisher);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);

        inOrder.verify(productClient).getProduct(1L);
        inOrder.verify(orderRepository).save(captor.capture());
        inOrder.verify(orderEventPublisher).publishOrderCreated(eventCaptor.capture());
        // Başka hiçbir çağrı olmamalı (ör. ikinci bir save ya da çift yayın)
        verifyNoMoreInteractions(productClient, orderRepository, orderEventPublisher);

        Order saved = captor.getValue();
        assertThat(saved.getProductId()).isEqualTo(1L);
        assertThat(saved.getQuantity()).isEqualTo(3);
        assertThat(response.totalPrice()).isEqualByComparingTo("300.00");

        assertThat(eventCaptor.getValue().productId()).isEqualTo(1L);
        assertThat(eventCaptor.getValue().quantity()).isEqualTo(3);
    }


    @Test
    @DisplayName("create: createOrder_insufficientStock")
    void createOrder_insufficientStock() {
        // given
        var product = new ProductDto(1L, "Ürün", new BigDecimal("100.00"), 2); // kendi DTO/Entity yapına göre
        when(productClient.getProduct(1L)).thenReturn(product);
        var request = new OrderRequest(product.id(), 5);
        // when + then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InsufficientStockException.class);
        // then
        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publishOrderCreated(any());

    }


    @Test
    @DisplayName("create: createOrder_productNotFound")
    void createOrder_productNotFound() {
// given
        var request = new OrderRequest(99L, 5);
        when(productClient.getProduct(99L))
                .thenThrow(new ProductNotFoundException(99L));

        // when + then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ProductNotFoundException.class);

        // then
        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publishOrderCreated(any());
        verifyNoMoreInteractions(orderRepository, orderEventPublisher);

    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L})
    @DisplayName("create: getById_notFound")
    void getById_notFound(Long id) {
// given
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> orderService.getById(id))
                .isInstanceOf(OrderNotFoundException.class);

        // then
        verify(orderRepository).findById(id);
        verifyNoMoreInteractions(orderRepository);

    }
}
