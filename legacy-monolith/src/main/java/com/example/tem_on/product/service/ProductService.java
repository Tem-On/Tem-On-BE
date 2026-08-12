package com.example.tem_on.product.service;

import com.example.tem_on.product.domain.dto.ProductResponse;
import com.example.tem_on.product.domain.entity.Product;
import com.example.tem_on.product.domain.entity.ProductCategory;
import com.example.tem_on.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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
        Page<Product> products;

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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return ProductResponse.from(product);
    }
}