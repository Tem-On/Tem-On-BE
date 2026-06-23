package com.example.temon.commerceservice.product.domain.dto;


import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import com.example.temon.commerceservice.product.domain.entity.Product;
import com.example.temon.commerceservice.product.domain.entity.ProductCategory;
import com.example.temon.commerceservice.product.domain.entity.ProductStatus;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private int price;
    private String imageUrl;
    private ProductCategory category;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
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