package com.baristopal.product;

import com.baristopal.product.domain.Product;
import com.baristopal.product.domain.ProductRepository;
import com.baristopal.product.messaging.OrderCreatedEvent;
import com.baristopal.product.messaging.OrderCreatedListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HAFTA 2 - ÖRNEK: @SpringBootTest tüm context'i yükler. Mock yok:
 * HTTP -> Controller -> Service -> JPA -> H2 zinciri gerçekten çalışır.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderCreatedListener orderCreatedListener;

    @BeforeEach
    void cleanDb() {
        productRepository.deleteAll();
    }

    @Test
    void createThenGet_endToEnd() throws Exception {
        String body = """
                {"name": "Monitör", "price": 4500.00, "stock": 3}
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Gerçekten veritabanına yazıldı mı?
        assertThat(productRepository.count()).isEqualTo(1);
        Long id = productRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Monitör"))
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    void orderCreatedEvent_decreasesStockInDatabase() {
        // RabbitMQ'yu test etmiyoruz; listener metodunu doğrudan çağırıp DB etkisini doğruluyoruz.
        Product saved = productRepository.save(new Product("Kulaklık", new BigDecimal("900.00"), 10));

        orderCreatedListener.onOrderCreated(new OrderCreatedEvent(1L, saved.getId(), 4));

        assertThat(productRepository.findById(saved.getId()).orElseThrow().getStock()).isEqualTo(6);
    }

    // TODO (Hafta 2 - alıştırma): Stok yetersizken GET/POST akışında 409 dönen bir senaryo ekle.
    // TODO (İleri seviye): H2 yerine Testcontainers ile gerçek PostgreSQL kullan.
}
