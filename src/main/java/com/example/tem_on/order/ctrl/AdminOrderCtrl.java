package com.example.tem_on.order.ctrl;

import com.example.tem_on.order.domain.dto.AdminOrderDetailResponse;
import com.example.tem_on.order.domain.dto.AdminOrderEventProductStatisticsResponse;
import com.example.tem_on.order.domain.dto.AdminOrderResponse;
import com.example.tem_on.order.domain.dto.AdminOrderStatisticsResponse;
import com.example.tem_on.order.domain.dto.AdminOrderStatusUpdateRequest;
import com.example.tem_on.order.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Order", description = "관리자 주문 관리 API")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderCtrl {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "전체 주문 목록 조회", description = "관리자가 전체 주문 목록을 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<AdminOrderResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                adminOrderService.getOrders(PageRequest.of(page, size))
        );
    }

    @Operation(summary = "주문 상세 조회", description = "관리자가 특정 주문의 상세 정보와 주문 상품 목록을 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderDetailResponse> getOrder(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(adminOrderService.getOrder(orderId));
    }

    @Operation(summary = "주문 상태 변경", description = "관리자가 CREATED 상태의 주문을 PAID 또는 CANCELED 상태로 변경합니다.")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderDetailResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody AdminOrderStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                adminOrderService.updateOrderStatus(orderId, request)
        );
    }

    @Operation(summary = "전체 주문/매출 통계 조회", description = "전체 주문 수, 상태별 주문 수, 결제 완료 기준 총 매출을 조회합니다.")
    @GetMapping("/statistics")
    public ResponseEntity<AdminOrderStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminOrderService.getStatistics());
    }

    @Operation(summary = "이벤트 상품별 주문/매출 통계 조회", description = "결제 완료된 주문 기준으로 이벤트 상품별 주문 수, 판매 수량, 매출을 조회합니다.")
    @GetMapping("/statistics/event-products")
    public ResponseEntity<List<AdminOrderEventProductStatisticsResponse>> getEventProductStatistics() {
        return ResponseEntity.ok(adminOrderService.getEventProductStatistics());
    }
}