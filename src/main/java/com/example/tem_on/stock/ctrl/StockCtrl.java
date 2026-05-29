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
    @Operation(summary = "상품별 재고 상태 상세 조회", description = "특정 이벤트 상품의 남은 수량, 선점 수량, 판매 수량을 조회합니다.")
    public ResponseEntity<StockEntity> getStock(@PathVariable Long eventProductId) {
        return ResponseEntity.ok(stockService.getStock(eventProductId));
    }

    @GetMapping("/{eventProductId}/sold-out")
    @Operation(summary = "상품 품절 여부 확인", description = "해당 이벤트 상품의 남은 재고가 0 이하인지 판별하여 품절 상태(true/false)를 반환합니다.")
    public ResponseEntity<Boolean> isSoldOut(@PathVariable Long eventProductId) {
        return ResponseEntity.ok(stockService.isSoldOut(eventProductId));
    }

    @PostMapping("/reserve")
    @Operation(summary = "재고 임시 선점 (구매 클릭 시)", description = "결제 진입 시 타인의 접근을 막기 위해 남은 재고를 차감하고 선점 수량을 늘립니다.")
    public ResponseEntity<String> reserve(@RequestBody StockRequest dto) {
        stockService.reserveStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("재고 임시 선점 성공");
    }

    @PostMapping("/release")
    @Operation(summary = "재고 선점 해제 (이탈/타임아웃 시)", description = "사용자가 결제를 진행하지 않고 이탈하거나 제한시간이 만료되었을 때 선점을 해제합니다.")
    public ResponseEntity<String> release(@RequestBody StockRequest dto) {
        stockService.releaseStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("재고 선점 해제 완료");
    }

    @PostMapping("/decrease")
    @Operation(summary = "재고 최종 차감 (결제 성공 시)", description = "결제 승인이 완료되었을 때 선점된 수량을 지우고 판매 완료 수량으로 확정합니다.")
    public ResponseEntity<String> decrease(@RequestBody StockRequest dto) {
        stockService.decreaseStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("결제 성공: 재고 최종 차감 완료");
    }

    @PostMapping("/restore")
    @Operation(summary = "재고 원상 복구 (결제 실패 시)", description = "카드 잔액 부족 등 결제 과정에서 실패했을 때 선점했던 재고를 다시 판매 가능 상태로 돌려놓습니다.")
    public ResponseEntity<String> restore(@RequestBody StockRequest dto) {
        stockService.restoreStock(dto.getEventProductId(), dto.getQuantity());
        return ResponseEntity.ok("결제 실패: 재고 원상 복구 완료");
    }
}