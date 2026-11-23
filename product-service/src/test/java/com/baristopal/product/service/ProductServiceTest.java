package com.baristopal.product.service;

import com.baristopal.product.domain.Product;
import com.baristopal.product.domain.ProductRepository;
import com.baristopal.product.exception.InsufficientStockException;
import com.baristopal.product.exception.ProductNotFoundException;
import com.baristopal.product.web.dto.ProductRequest;
import com.baristopal.product.web.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * HAFTA 1 - ÖRNEK: Saf birim testi. Spring context yok, veritabanı yok.
 * Repository Mockito ile taklit ediliyor; sadece ProductService'in mantığı test ediliyor.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("create: ürünü kaydeder ve response döner")
    void create_savesProduct() {
        // given
        var request = new ProductRequest("Klavye", new BigDecimal("750.00"), 10);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ProductResponse response = productService.create(request);

        // then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Klavye");
        assertThat(response.stock()).isEqualTo(10);
    }

    @Test
    @DisplayName("getById: ürün yoksa ProductNotFoundException fırlatır")
    void getById_throwsWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("decreaseStock: stok yeterliyse düşer")
    void decreaseStock_reducesStock() {
        var product = new Product("Mouse", new BigDecimal("300.00"), 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.decreaseStock(1L, 3);

        assertThat(product.getStock()).isEqualTo(2);
    }

    @Test
    @DisplayName("decreaseStock: stok yetersizse hata fırlatır ve stok değişmez")
    void decreaseStock_throwsWhenInsufficient() {
        var product = new Product("Mouse", new BigDecimal("300.00"), 2);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.decreaseStock(1L, 5))
                .isInstanceOf(InsufficientStockException.class);
        assertThat(product.getStock()).isEqualTo(2);
    }

    // TODO (Hafta 1 - alıştırma): list() için bir test yaz.
    // TODO (Hafta 1 - alıştırma): decreaseStock(1L, 0) çağrısının IllegalArgumentException
    //  fırlattığını test et. İpucu: @ParameterizedTest + @ValueSource(ints = {0, -1})
}
