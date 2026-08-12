package com.example.temon.queuestockservice.stock.ctrl;

import com.example.temon.queuestockservice.stock.domain.dto.StockRequest;
import com.example.temon.queuestockservice.stock.domain.entity.StockEntity;
import com.example.temon.queuestockservice.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/stocks")
@RequiredArgsConstructor
public class InternalStockCtrl {

    private final StockService stockService;

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveStock(
            @RequestBody StockRequest request
    ) {
        stockService.reserveStock(
                request.getEventProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(
                "재고 선점 완료"
        );
    }

    @PostMapping("/release")
    public ResponseEntity<String> releaseStock(
            @RequestBody StockRequest request
    ) {
        stockService.releaseStock(
                request.getEventProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(
                "재고 복구 완료"
        );
    }

    @GetMapping("/bulk")
    public ResponseEntity<List<StockEntity>> getStocksByProductIds(
            @RequestParam("eventProductIds")
            List<Long> eventProductIds
    ) {
        return ResponseEntity.ok(
                stockService.getStocksByEventProductIds(
                        eventProductIds
                )
        );
    }
}