package com.example.tem_on.product.service;

import com.example.tem_on.product.domain.dto.ProductResponse;
import com.example.tem_on.product.domain.entity.Product;
import com.example.tem_on.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 목록 조회
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    // 상품 상세 조회
    public ProductResponse findProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return ProductResponse.from(product);
    }
}