package com.example.temon.orderpaymentservice.order.ctrl;

import com.example.temon.orderpaymentservice.global.jwt.ApiUserPrincipal;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderCreateRequest;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderItemResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderResponse;
import com.example.temon.orderpaymentservice.order.facade.OrderFacade;
import com.example.temon.orderpaymentservice.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 API")
public class OrderCtrl {

    private final OrderFacade orderFacade;
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "JWT 인증 사용자 기준으로 주문을 생성하고 재고를 임시 선점합니다.")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal,
            @RequestBody OrderCreateRequest request
    ) {
        return ResponseEntity.ok(
                orderFacade.createOrder(userPrincipal.getUserId(), request)
        );
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 정보를 조회합니다.")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/me")
    @Operation(summary = "내 주문 목록 조회", description = "현재 로그인한 사용자의 주문 목록을 조회합니다.")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                orderService.getMyOrders(userPrincipal.getUserId())
        );
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "현재 로그인한 사용자의 주문을 취소합니다.")
    public ResponseEntity<String> cancelOrder(
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal,
            @PathVariable Long orderId
    ) {
        orderFacade.cancelOrder(userPrincipal.getUserId(), orderId);

        return ResponseEntity.ok("주문 취소 완료");
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "주문 상품 조회", description = "주문에 포함된 상품 목록을 조회합니다.")
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }
}