package com.example.tem_on.product.domain.dto;

import com.example.tem_on.product.domain.entity.ProductCategory;
import com.example.tem_on.product.domain.entity.ProductStatus;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ProductUpdateRequest {

    private String name;

    private String description;

    @Positive(message = "상품 가격은 0보다 커야 합니다.")
    private Integer price;

    private String imageUrl;

    private ProductCategory category;

    private ProductStatus status;
}