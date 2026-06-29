package com.example.temon.orderpaymentservice.order.service;

import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderDetailResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderEventProductStatisticsResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderStatisticsResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderStatusUpdateRequest;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderEntity;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import com.example.temon.orderpaymentservice.order.repository.OrderItemRepository;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Page<AdminOrderResponse> getOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByOrderedAtDesc(pageable)
                .map(AdminOrderResponse::from);
    }

    public AdminOrderDetailResponse getOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        return AdminOrderDetailResponse.from(order);
    }

    @Transactional
    public AdminOrderDetailResponse updateOrderStatus(
            Long orderId,
            AdminOrderStatusUpdateRequest request
    ) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        validateStatusChange(order, request.getStatus());

        order.changeStatus(request.getStatus());

        return AdminOrderDetailResponse.from(order);
    }

    public AdminOrderStatisticsResponse getStatistics() {
        return AdminOrderStatisticsResponse.builder()
                .totalOrderCount(orderRepository.count())
                .createdOrderCount(orderRepository.countByStatus(OrderStatus.CREATED))
                .paidOrderCount(orderRepository.countByStatus(OrderStatus.PAID))
                .canceledOrderCount(orderRepository.countByStatus(OrderStatus.CANCELED))
                .totalSales(orderRepository.sumTotalAmountByStatus(OrderStatus.PAID))
                .build();
    }

    public List<AdminOrderEventProductStatisticsResponse> getEventProductStatistics() {
        return orderItemRepository.findEventProductStatisticsByOrderStatus(OrderStatus.PAID);
    }

    private void validateStatusChange(OrderEntity order, OrderStatus newStatus) {
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("CREATED 상태의 주문만 변경할 수 있습니다.");
        }

        if (newStatus == OrderStatus.CREATED) {
            throw new IllegalArgumentException("CREATED 상태로는 변경할 수 없습니다.");
        }
    }
}