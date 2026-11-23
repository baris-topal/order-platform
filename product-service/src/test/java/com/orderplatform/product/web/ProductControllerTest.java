package com.orderplatform.product.web;

import com.orderplatform.product.exception.ProductNotFoundException;
import com.orderplatform.product.service.ProductService;
import com.orderplatform.product.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HAFTA 1 - ÖRNEK: @WebMvcTest sadece web katmanını (controller, advice, JSON, validation) yükler.
 * Servis katmanı @MockBean ile taklit edilir; veritabanı ayağa kalkmaz.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getById_returnsProduct() throws Exception {
        when(productService.getById(1L))
                .thenReturn(new ProductResponse(1L, "Klavye", new BigDecimal("750.00"), 10));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Klavye"))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void getById_returns404WhenMissing() throws Exception {
        when(productService.getById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product not found: 99"));
    }

    @Test
    void create_returns400WhenNameBlank() throws Exception {
        String body = """
                {"name": "", "price": 10.00, "stock": 1}
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(productService, never()).create(any());
    }

    @Test
    void create_returns201() throws Exception {
        // given
        String body = """
            {"name": "test", "price": 10.00, "stock": 1}
            """;
        when(productService.create(any()))
                .thenReturn(new ProductResponse(1L, "test", new BigDecimal("10.00"), 1));

        // when + then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.stock").value(1));

        verify(productService).create(any());
    }
}
