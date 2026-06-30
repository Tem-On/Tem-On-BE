package com.example.temon.orderpaymentservice.payment.ctrl;

import com.example.temon.orderpaymentservice.global.jwt.ApiUserPrincipal;
import com.example.temon.orderpaymentservice.payment.domain.dto.PaymentRequest;
import com.example.temon.orderpaymentservice.payment.domain.dto.PaymentResponse;
import com.example.temon.orderpaymentservice.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 API")
public class PaymentCtrl {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "결제 요청", description = "현재 로그인한 사용자의 주문을 결제 요청합니다.")
    public ResponseEntity<PaymentResponse> requestPayment(
            @AuthenticationPrincipal ApiUserPrincipal userPrincipal,
            @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(
                paymentService.requestPayment(userPrincipal.getUserId(), request)
        );
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 조회", description = "결제 정보를 조회합니다.")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.getPayment(paymentId)
        );
    }

    @PostMapping("/{paymentId}/success")
    @Operation(summary = "결제 성공", description = "결제를 성공 처리합니다.")
    public ResponseEntity<PaymentResponse> success(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.success(paymentId)
        );
    }

    @PostMapping("/{paymentId}/fail")
    @Operation(summary = "결제 실패", description = "결제를 실패 처리합니다.")
    public ResponseEntity<PaymentResponse> fail(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.fail(paymentId)
        );
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "결제 취소", description = "결제를 취소합니다.")
    public ResponseEntity<PaymentResponse> cancel(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.cancel(paymentId)
        );
    }
}