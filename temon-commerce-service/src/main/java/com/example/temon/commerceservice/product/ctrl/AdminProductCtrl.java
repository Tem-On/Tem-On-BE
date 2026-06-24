package com.example.temon.commerceservice.product.ctrl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.temon.commerceservice.product.domain.dto.AdminProductResponse;
import com.example.temon.commerceservice.product.domain.dto.ProductCreateRequest;
import com.example.temon.commerceservice.product.domain.dto.ProductUpdateRequest;
import com.example.temon.commerceservice.product.service.AdminProductService;
import org.springframework.data.domain.Sort;

@Tag(name = "Admin Product", description = "관리자 상품 관리 API")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductCtrl {

    private final AdminProductService adminProductService;

    @Operation(
            summary = "상품 등록",
            description = "관리자가 새로운 상품을 등록합니다."
    )
    @PostMapping
    public ResponseEntity<AdminProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        return ResponseEntity.ok(
                adminProductService.createProduct(request)
        );
    }

    @Operation(
            summary = "상품 목록 조회",
            description = "삭제되지 않은 상품 목록을 페이지네이션으로 조회합니다."
    )
    @GetMapping
    public ResponseEntity<Page<AdminProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        return ResponseEntity.ok(
                adminProductService.getProducts(pageable)
        );
    }

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 ID를 기준으로 상품 상세 정보를 조회합니다."
    )
    @GetMapping("/{productId}")
    public ResponseEntity<AdminProductResponse> getProduct(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                adminProductService.getProduct(productId)
        );
    }

    @Operation(
            summary = "상품 수정",
            description = "상품명, 설명, 가격, 이미지, 카테고리, 상태를 수정합니다."
    )
    @PatchMapping("/{productId}")
    public ResponseEntity<AdminProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        return ResponseEntity.ok(
                adminProductService.updateProduct(productId, request)
        );
    }

    @Operation(
            summary = "상품 삭제",
            description = "상품을 실제 삭제하지 않고 DELETED 상태로 변경합니다."
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId
    ) {
        adminProductService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }
}