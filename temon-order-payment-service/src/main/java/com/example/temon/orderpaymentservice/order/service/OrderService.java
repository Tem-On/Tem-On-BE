package com.example.temon.orderpaymentservice.order.service;

import com.example.temon.orderpaymentservice.order.domain.dto.OrderItemResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderResponse;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderEntity;
import com.example.temon.orderpaymentservice.order.repository.OrderItemRepository;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;
import com.example.temon.orderpaymentservice.payment.domain.entity.PaymentEntity;
import com.example.temon.orderpaymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final PaymentRepository paymentRepository;

    public OrderResponse getOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "주문을 찾을 수 없습니다."
                        )
                );

        return toOrderResponse(order);
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderedAtDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    public List<OrderItemResponse> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(OrderItemResponse::from)
                .toList();
    }

    /**
     * 주문과 연결된 결제 ID까지 포함해 응답으로 변환합니다.
     *
     * 아직 결제 요청이 만들어지지 않은 주문이라면
     * paymentId는 null로 반환됩니다.
     */
    private OrderResponse toOrderResponse(OrderEntity order) {
        Long paymentId = paymentRepository
                .findByOrderId(order.getId())
                .map(PaymentEntity::getId)
                .orElse(null);

        return OrderResponse.from(order, paymentId);
    }
}