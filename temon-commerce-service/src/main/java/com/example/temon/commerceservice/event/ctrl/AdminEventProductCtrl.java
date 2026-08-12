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

@Tag(
        name = "Admin EventProduct",
        description = "관리자 - 이벤트 상품 관리 API"
)
@RestController
@RequestMapping("/api/admin/event-products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventProductCtrl {

    private final AdminEventProductService adminEventProductService;

    @PostMapping
    @Operation(summary = "이벤트 상품 등록")
    public ResponseEntity<EventProductResponse> createEventProduct(
            @RequestBody EventProductCreateRequest request
    ) {
        EventProductResponse response =
                adminEventProductService.createEventProduct(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "이벤트 상품 목록 조회")
    public ResponseEntity<List<EventProductResponse>> getEventProductList() {
        return ResponseEntity.ok(
                adminEventProductService.getEventProductList()
        );
    }

    @GetMapping("/{eventProductId}")
    @Operation(summary = "이벤트 상품 상세 조회")
    public ResponseEntity<EventProductResponse> getEventProductDetail(
            @PathVariable Long eventProductId
    ) {
        return ResponseEntity.ok(
                adminEventProductService.getEventProductDetail(
                        eventProductId
                )
        );
    }

    @PatchMapping("/{eventProductId}")
    @Operation(summary = "이벤트 상품 수정")
    public ResponseEntity<EventProductResponse> updateEventProduct(
            @PathVariable Long eventProductId,
            @RequestBody EventProductUpdateRequest request
    ) {
        return ResponseEntity.ok(
                adminEventProductService.updateEventProduct(
                        eventProductId,
                        request
                )
        );
    }

    @PatchMapping("/{eventProductId}/status")
    @Operation(summary = "이벤트 상품 상태 변경")
    public ResponseEntity<EventProductResponse> updateEventProductStatus(
            @PathVariable Long eventProductId,
            @RequestBody EventProductStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                adminEventProductService.updateEventProductStatus(
                        eventProductId,
                        request
                )
        );
    }

    @DeleteMapping("/{eventProductId}")
    @Operation(summary = "이벤트 상품 삭제")
    public ResponseEntity<String> deleteEventProduct(
            @PathVariable Long eventProductId
    ) {
        adminEventProductService.deleteEventProduct(
                eventProductId
        );

        return ResponseEntity.ok(
                "이벤트 상품이 정상적으로 삭제되었습니다."
        );
    }
}