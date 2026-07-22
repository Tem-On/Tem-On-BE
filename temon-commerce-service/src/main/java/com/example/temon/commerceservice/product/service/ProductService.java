package com.example.temon.commerceservice.product.service;

import com.example.temon.commerceservice.product.domain.dto.ProductResponse;
import com.example.temon.commerceservice.product.domain.entity.ProductCategory;
import com.example.temon.commerceservice.product.domain.entity.ProductEntity;
import com.example.temon.commerceservice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> findProducts(
            ProductCategory category,
            String keyword,
            Pageable pageable
    ) {
        Page<ProductEntity> products;

        boolean hasCategory = category != null;
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (hasCategory && hasKeyword) {
            products = productRepository.findByCategoryAndNameContaining(category, keyword, pageable);
        } else if (hasCategory) {
            products = productRepository.findByCategory(category, pageable);
        } else if (hasKeyword) {
            products = productRepository.findByNameContaining(keyword, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(ProductResponse::from);
    }

    public ProductResponse findProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return ProductResponse.from(product);
    }

    public List<ProductResponse> getProductsByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
        return List.of();
    }

    return productRepository.findAllById(ids)
            .stream()
            .map(ProductResponse::from) 
            .toList();
}
}