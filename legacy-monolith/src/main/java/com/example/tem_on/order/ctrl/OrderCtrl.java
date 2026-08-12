package com.example.tem_on.order.ctrl;

import com.example.tem_on.order.domain.dto.OrderCreateRequest;
import com.example.tem_on.order.domain.dto.OrderItemResponse;
import com.example.tem_on.order.domain.dto.OrderResponse;
import com.example.tem_on.order.facade.OrderFacade;
import com.example.tem_on.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication authentication,
            @RequestBody OrderCreateRequest request
    ) {
        Long userId = Long.parseLong(authentication.getName());

        return ResponseEntity.ok(orderFacade.createOrder(userId, request));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/me")
    @Operation(summary = "내 주문 목록 조회")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소")
    public ResponseEntity<String> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        Long userId = Long.parseLong(authentication.getName());

        orderFacade.cancelOrder(userId, orderId);

        return ResponseEntity.ok("주문 취소 완료");
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "주문 상품 조회")
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }
}