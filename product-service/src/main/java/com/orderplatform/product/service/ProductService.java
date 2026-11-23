package com.orderplatform.product.service;

import com.orderplatform.product.domain.Product;
import com.orderplatform.product.domain.ProductRepository;
import com.orderplatform.product.exception.ProductNotFoundException;
import com.orderplatform.product.web.dto.ProductRequest;
import com.orderplatform.product.web.dto.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product saved = productRepository.save(
                new Product(request.name(), request.price(), request.stock()));
        return ProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = findOrThrow(productId);
        product.decreaseStock(quantity);
        // Managed entity olduğu için save çağırmaya gerek yok (dirty checking).
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
