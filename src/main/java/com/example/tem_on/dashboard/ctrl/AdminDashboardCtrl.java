package com.example.tem_on.dashboard.ctrl;

import com.example.tem_on.dashboard.domain.dto.AdminDashboardResponse;
import com.example.tem_on.dashboard.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Dashboard", description = "관리자 대시보드 API")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
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
}