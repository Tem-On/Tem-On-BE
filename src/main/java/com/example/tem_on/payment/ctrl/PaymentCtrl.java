package com.example.tem_on.payment.ctrl;

import com.example.tem_on.auth.jwt.CustomUserDetails;
import com.example.tem_on.payment.domain.dto.PaymentRequest;
import com.example.tem_on.payment.domain.dto.PaymentResponse;
import com.example.tem_on.payment.service.PaymentService;

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
    @Operation(summary = "결제 요청")
    public ResponseEntity<PaymentResponse> requestPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(
                paymentService.requestPayment(
                        userDetails.getUserId(),
                        request
                )
        );
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 조회")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.getPayment(paymentId)
        );
    }

    @PostMapping("/{paymentId}/success")
    @Operation(summary = "결제 성공")
    public ResponseEntity<PaymentResponse> success(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.success(paymentId)
        );
    }

    @PostMapping("/{paymentId}/fail")
    @Operation(summary = "결제 실패")
    public ResponseEntity<PaymentResponse> fail(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.fail(paymentId)
        );
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "결제 취소")
    public ResponseEntity<PaymentResponse> cancel(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.cancel(paymentId)
        );
    }
}