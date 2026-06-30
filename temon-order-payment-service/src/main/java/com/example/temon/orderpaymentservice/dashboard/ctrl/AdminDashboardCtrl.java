package com.example.temon.orderpaymentservice.dashboard.ctrl;

import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminDashboardResponse;
import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminEventDashboardResponse;
import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminSalesDashboardResponse;
import com.example.temon.orderpaymentservice.dashboard.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Dashboard", description = "관리자 대시보드 API")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardCtrl {

    private final AdminDashboardService adminDashboardService;

    @Operation(
            summary = "관리자 대시보드 조회",
            description = "전체 주문 수, 결제 완료 수, 취소 수, 총 매출, 진행 이벤트 수, 품절 상품 수, 총 판매 수량을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminDashboardService.getDashboard());
    }

    @Operation(
            summary = "이벤트별 대시보드 조회",
            description = "특정 이벤트의 이벤트상품 수, 총 매출, 총 판매 수량을 조회합니다."
    )
    @GetMapping("/events/{eventId}")
    public ResponseEntity<AdminEventDashboardResponse> getEventDashboard(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(adminDashboardService.getEventDashboard(eventId));
    }

    @Operation(
            summary = "매출 통계 조회",
            description = "총 매출, 결제 완료 주문 수, 평균 주문 금액을 조회합니다."
    )
    @GetMapping("/sales")
    public ResponseEntity<AdminSalesDashboardResponse> getSalesDashboard() {
        return ResponseEntity.ok(adminDashboardService.getSalesDashboard());
    }
}