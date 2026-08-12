package com.example.temon.orderpaymentservice.order.service;

import com.example.temon.orderpaymentservice.global.client.CommerceServiceClient;
import com.example.temon.orderpaymentservice.global.client.EventProductResponse;
import com.example.temon.orderpaymentservice.global.client.ProductResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderDetailResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderEventProductStatisticsResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderStatisticsResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderStatusUpdateRequest;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderEntity;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderItemEntity;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import com.example.temon.orderpaymentservice.order.repository.OrderItemRepository;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CommerceServiceClient commerceServiceClient;


    public Page<AdminOrderResponse> getOrders(Pageable pageable) {
        Page<OrderEntity> orderPage = orderRepository.findAllByOrderByOrderedAtDesc(pageable);
        if (orderPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> eventProductIds = orderPage.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItemEntity::getEventProductId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();


 
        Map<Long, String> eventProductNameMap = createEventProductNameMap(eventProductIds);


        return orderPage.map(order -> {
            String displayProductName = resolveProductName(order.getOrderItems(), eventProductNameMap);
            return AdminOrderResponse.from(order, displayProductName);
        });
    }

    public AdminOrderDetailResponse getOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        return AdminOrderDetailResponse.from(order);
    }

    @Transactional
    public AdminOrderDetailResponse updateOrderStatus(
            Long orderId,
            AdminOrderStatusUpdateRequest request) {
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

    private Map<Long, String> createEventProductNameMap(List<Long> eventProductIds) {
        if (eventProductIds == null || eventProductIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, EventProductResponse> eventProductMap = fetchEventProducts(eventProductIds);

        List<Long> productIds = eventProductMap.values().stream()
                .map(EventProductResponse::productId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> productNameMap = fetchProductNames(productIds);

        return eventProductMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Long productId = entry.getValue().productId();
                            return (productId != null) 
                                    ? productNameMap.getOrDefault(productId, "알 수 없는 상품") 
                                    : "알 수 없는 상품";
                        },
                        (p1, p2) -> p1
                ));
    }


    private String resolveProductName(
            List<OrderItemEntity> orderItems,
            Map<Long, String> eventProductNameMap
    ) {
        if (orderItems == null || orderItems.isEmpty()) {
            return "상품 정보 없음";
        }

        String firstProductName = eventProductNameMap.getOrDefault(
                orderItems.get(0).getEventProductId(), 
                "상품 정보 없음"
        );

        int extraCount = orderItems.size() - 1;
        return extraCount > 0 ? firstProductName + " 외 " + extraCount + "건" : firstProductName;
    }


    private Map<Long, EventProductResponse> fetchEventProducts(List<Long> eventProductIds) {
        try {
            List<EventProductResponse> eventProducts = commerceServiceClient.getEventProductsByIds(eventProductIds);
            if (eventProducts == null) return Collections.emptyMap();

            return eventProducts.stream()
                    .collect(Collectors.toMap(
                            EventProductResponse::id, 
                            ep -> ep, 
                            (e1, e2) -> e1
                    ));
        } catch (Exception e) {

            return Collections.emptyMap();
        }
    }

    private Map<Long, String> fetchProductNames(List<Long> productIds) {
        if (productIds.isEmpty()) return Collections.emptyMap();

        try {
            List<ProductResponse> products = commerceServiceClient.getProductsByIds(productIds);
            if (products == null) return Collections.emptyMap();

            return products.stream()
                    .collect(Collectors.toMap(ProductResponse::id, ProductResponse::name, (p1, p2) -> p1));
        } catch (Exception e) {

            return Collections.emptyMap();
        }
    }
}