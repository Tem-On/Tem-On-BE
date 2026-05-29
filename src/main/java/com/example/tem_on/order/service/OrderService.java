package com.example.tem_on.order.service;

import com.example.tem_on.order.domain.dto.OrderItemResponse;
import com.example.tem_on.order.domain.dto.OrderResponse;
import com.example.tem_on.order.domain.entity.OrderEntity;
import com.example.tem_on.order.repository.OrderItemRepository;
import com.example.tem_on.order.repository.OrderRepository;
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

    public OrderResponse getOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        return OrderResponse.from(order);
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderedAtDesc(userId)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    public List<OrderItemResponse> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(OrderItemResponse::from)
                .toList();
    }
}