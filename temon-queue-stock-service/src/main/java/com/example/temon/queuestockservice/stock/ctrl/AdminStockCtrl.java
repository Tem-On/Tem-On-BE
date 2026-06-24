package com.example.temon.queuestockservice.stock.ctrl;

import com.example.temon.queuestockservice.stock.domain.dto.StockRequest;
import com.example.temon.queuestockservice.stock.domain.dto.StockResponse;
import com.example.temon.queuestockservice.stock.domain.dto.StockUpdateRequest;
import com.example.temon.queuestockservice.stock.service.AdminStockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stocks")
@RequiredArgsConstructor
@Tag(name = "Admin Stock", description = "관리자 전용 재고 관리 및 강제 제어 API")
public class AdminStockCtrl {

    private final AdminStockService adminStockService;

    @PostMapping
    @Operation(summary = "새로운 이벤트 상품 재고 초기 등록")
    public ResponseEntity<String> createStock(@RequestBody StockRequest request) {
        adminStockService.createStock(request);
        return ResponseEntity.ok("이벤트 상품 재고 등록 성공");
    }

    @GetMapping
    @Operation(summary = "전체 이벤트 상품 재고 목록 조회")
    public ResponseEntity<List<StockResponse>> getStockList() {
        List<StockResponse> response = adminStockService.getStockList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{eventProductId}")
    @Operation(summary = "특정 이벤트 상품 재고 상태 상세 조회")
    public ResponseEntity<StockResponse> getStockDetail(@PathVariable Long eventProductId) {
        StockResponse response = adminStockService.getStockDetail(eventProductId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{eventProductId}")
    @Operation(summary = "관리자 권한 총 재고 수량 직접 수정", description = "URL 주소로 대상을 식별하고, Body에는 수정할 수량(quantity)만 보냅니다.")
    public ResponseEntity<String> updateStockQuantity(
            @PathVariable Long eventProductId, 
            @RequestBody StockUpdateRequest request) { 
        adminStockService.updateStockQuantity(eventProductId, request.getQuantity());
        return ResponseEntity.ok("재고 수량 수정 완료");
    }

    @PatchMapping("/{eventProductId}/sold-out")
    @Operation(summary = "이벤트 상품 강제 품절(남은 재고 0) 처리")
    public ResponseEntity<String> forceSoldOut(@PathVariable Long eventProductId) {
        adminStockService.forceSoldOut(eventProductId);
        return ResponseEntity.ok("강제 품절 처리 완료");
    }
}