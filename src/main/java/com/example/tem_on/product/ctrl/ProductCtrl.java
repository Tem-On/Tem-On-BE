package com.example.tem_on.product.ctrl;

import com.example.tem_on.product.domain.dto.ProductResponse;
import com.example.tem_on.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCtrl {

    private final ProductService productService;

    @Operation(
            summary = "상품 목록 조회",
            description = "기본 상품 목록을 조회합니다."
    )
    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.findAllProducts();
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