package com.example.temon.commerceservice.event.ctrl;

import com.example.temon.commerceservice.event.domain.dto.EventProductCreateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventProductResponse;
import com.example.temon.commerceservice.event.domain.dto.EventProductStatusUpdateRequest;
import com.example.temon.commerceservice.event.domain.dto.EventProductUpdateRequest;
import com.example.temon.commerceservice.event.service.AdminEventProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin EventProduct", description = "관리자 - 이벤트 상품 관리 API")
@RestController
@RequestMapping("/api/admin/event-products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventProductCtrl {

    private final AdminEventProductService adminEventProductService;

    @PostMapping
    @Operation(summary = "이벤트 상품 등록")
    public ResponseEntity<String> createEventProduct(@RequestBody EventProductCreateRequest request) {
        adminEventProductService.createEventProduct(request);
        return ResponseEntity.ok("이벤트 상품이 성공적으로 등록되었습니다.");
    }

    @GetMapping
    @Operation(summary = "이벤트 상품 목록 조회")
    public ResponseEntity<List<EventProductResponse>> getEventProductList() {
        List<EventProductResponse> products = adminEventProductService.getEventProductList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{eventProductId}")
    @Operation(summary = "이벤트 상품 상세 조회")
    public ResponseEntity<EventProductResponse> getEventProductDetail(
            @PathVariable("eventProductId") Long eventProductId
    ) {
        EventProductResponse product = adminEventProductService.getEventProductDetail(eventProductId);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{eventProductId}")
    @Operation(summary = "이벤트 상품 수정")
    public ResponseEntity<String> updateEventProduct(
            @PathVariable("eventProductId") Long eventProductId,
            @RequestBody EventProductUpdateRequest request) {
        adminEventProductService.updateEventProduct(eventProductId, request);
        return ResponseEntity.ok("이벤트 상품 정보가 수정되었습니다.");
    }

    @PatchMapping("/{eventProductId}/status")
    @Operation(summary = "이벤트 상품 상태 변경")
    public ResponseEntity<String> updateEventProductStatus(
            @PathVariable("eventProductId") Long eventProductId,
            @RequestBody EventProductStatusUpdateRequest request) {
        adminEventProductService.updateEventProductStatus(eventProductId, request);
        return ResponseEntity.ok("이벤트 상품 상태가 변경되었습니다.");
    }

    @DeleteMapping("/{eventProductId}")
    @Operation(summary = "이벤트 상품 삭제")
    public ResponseEntity<String> deleteEventProduct(@PathVariable("eventProductId") Long eventProductId) {
        adminEventProductService.deleteEventProduct(eventProductId);
        return ResponseEntity.ok("이벤트 상품이 정상적으로 삭제되었습니다.");
    }
}