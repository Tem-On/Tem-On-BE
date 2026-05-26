package com.example.tem_on.product.ctrl;

import com.example.tem_on.product.domain.dto.ProductResponse;
import com.example.tem_on.product.domain.entity.ProductCategory;
import com.example.tem_on.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCtrl {

    private final ProductService productService;

    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회합니다. 카테고리, 검색어, 페이지네이션, 정렬을 사용할 수 있습니다."
    )
    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return productService.findProducts(category, keyword, pageable);
    }

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 ID를 통해 상품 상세 정보를 조회합니다."
    )
    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return productService.findProduct(productId);
    }
}