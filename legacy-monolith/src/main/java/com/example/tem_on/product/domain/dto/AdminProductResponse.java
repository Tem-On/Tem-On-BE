package com.example.tem_on.product.domain.dto;

import com.example.tem_on.product.domain.entity.Product;
import com.example.tem_on.product.domain.entity.ProductCategory;
import com.example.tem_on.product.domain.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminProductResponse {

    private Long id;
    private String name;
    private String description;
    private int price;
    private String imageUrl;
    private ProductCategory category;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminProductResponse from(Product product) {
        return AdminProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}