package com.baristopal.order.client;

import java.math.BigDecimal;

/** product-service'in döndüğü JSON'un bu servisteki karşılığı. */
public record ProductDto(Long id, String name, BigDecimal price, int stock) {
}
