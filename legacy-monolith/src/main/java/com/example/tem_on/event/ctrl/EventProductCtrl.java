package com.example.tem_on.event.ctrl;

import com.example.tem_on.event.domain.dto.EventProductResponse;
import com.example.tem_on.event.service.EventProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Event Product", description = "이벤트 상품 관련 API")
public class EventProductCtrl {

    private final EventProductService eventProductService;

    @GetMapping("/api/event-products")
    @Operation(summary = "이벤트 상품 검색/필터", description = "전체 이벤트 상품 목록을 상세 상품 정보와 함께 조회합니다.")
    public ResponseEntity<List<EventProductResponse>> getAllEventProducts() {
        return ResponseEntity.ok(eventProductService.getAllEventProducts());
    }

    @GetMapping("/api/event-products/{eventProductId}")
    @Operation(summary = "이벤트 상품 상세 조회", description = "고유 ID로 이벤트 상품 및 매핑된 실제 상품의 상세 정보를 조회합니다.")
    public ResponseEntity<EventProductResponse> getEventProductDetail(@PathVariable Long eventProductId) {
        return ResponseEntity.ok(eventProductService.getEventProductDetail(eventProductId));
    }

    @GetMapping("/api/events/{eventId}/products")
    @Operation(summary = "특정 이벤트의 상품 목록 조회", description = "지정한 이벤트에 포함된 모든 상품들을 상세 정보와 함께 조회합니다.")
    public ResponseEntity<List<EventProductResponse>> getProductsByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventProductService.getProductsByEventId(eventId));
    }
}