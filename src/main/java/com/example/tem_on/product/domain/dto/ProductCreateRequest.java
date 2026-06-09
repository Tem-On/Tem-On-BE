package com.example.tem_on.product.domain.dto;

import com.example.tem_on.product.domain.entity.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    private String description;

    @NotNull(message = "상품 가격은 필수입니다.")
    @Positive(message = "상품 가격은 0보다 커야 합니다.")
    private Integer price;

    private String imageUrl;

    @NotNull(message = "상품 카테고리는 필수입니다.")
    private ProductCategory category;
}