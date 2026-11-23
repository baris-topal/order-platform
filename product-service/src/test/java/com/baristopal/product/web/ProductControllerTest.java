package com.baristopal.product.web;

import com.baristopal.product.exception.ProductNotFoundException;
import com.baristopal.product.service.ProductService;
import com.baristopal.product.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    // TODO (Hafta 1 - alıştırma): Başarılı POST için 201 Created ve dönen JSON'u doğrulayan test yaz.
}
