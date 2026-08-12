package com.example.tem_on.stock.ctrl;

import com.example.tem_on.stock.domain.dto.StockRequest;
import com.example.tem_on.stock.domain.entity.StockEntity;
import com.example.tem_on.stock.service.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "한정 수량 선착순 재고 제어 API")
public class StockCtrl {

    private final StockService stockService;

    @GetMapping("/{eventProductId}")
    @Operation(summary = "상품별 재고 상태 상세 조회")
    public ResponseEntity<StockEntity> getStock(@PathVariable Long eventProductId) {
        return ResponseEntity.ok(stockService.getStock(eventProductId));
    }

    @GetMapping("/{eventProductId}/sold-out")
    @Operation(summary = "상품 품절 여부 확인")
    public ResponseEntity<Boolean> isSoldOut(@PathVariable Long eventProductId) {
        return ResponseEntity.ok(stockService.isSoldOut(eventProductId));
    }

    @PostMapping("/reserve")
    @Operation(summary = "재고 임시 선점")
    public ResponseEntity<String> reserve(@RequestBody StockRequest dto) {
        stockService.reserveStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("재고 임시 선점 성공");
    }

    @PostMapping("/release")
    @Operation(summary = "재고 선점 해제")
    public ResponseEntity<String> release(@RequestBody StockRequest dto) {
        stockService.releaseStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("재고 선점 해제 완료");
    }

    @PostMapping("/confirm")
    @Operation(summary = "재고 판매 확정")
    public ResponseEntity<String> confirm(@RequestBody StockRequest dto) {
        stockService.confirmStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("재고 판매 확정 완료");
    }

    @PostMapping("/cancel-sold")
    @Operation(summary = "판매 재고 취소 복구")
    public ResponseEntity<String> cancelSold(@RequestBody StockRequest dto) {
        stockService.cancelSoldStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("판매 재고 취소 복구 완료");
    }
}