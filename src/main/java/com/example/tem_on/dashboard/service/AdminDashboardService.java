package com.example.tem_on.dashboard.service;

import com.example.tem_on.dashboard.domain.dto.AdminDashboardResponse;
import com.example.tem_on.dashboard.domain.dto.AdminEventDashboardResponse;
import com.example.tem_on.dashboard.domain.dto.AdminSalesDashboardResponse;
import com.example.tem_on.event.domain.entity.EventEntity;
import com.example.tem_on.event.domain.entity.EventProductEntity;
import com.example.tem_on.event.domain.entity.EventProductStatus;
import com.example.tem_on.event.domain.entity.EventStatus;
import com.example.tem_on.event.repository.EventProductRepository;
import com.example.tem_on.event.repository.EventRepository;
import com.example.tem_on.order.domain.entity.OrderStatus;
import com.example.tem_on.order.repository.OrderItemRepository;
import com.example.tem_on.order.repository.OrderRepository;
import com.example.tem_on.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EventRepository eventRepository;
    private final EventProductRepository eventProductRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        return AdminDashboardResponse.builder()
                .totalOrders(orderRepository.count())
                .paidOrders(orderRepository.countByStatus(OrderStatus.PAID))
                .canceledOrders(orderRepository.countByStatus(OrderStatus.CANCELED))
                .totalSales(orderRepository.sumTotalAmountByStatus(OrderStatus.PAID))
                .activeEvents(eventRepository.countByStatus(EventStatus.OPEN))
                .soldOutEventProducts(eventProductRepository.countByStatus(EventProductStatus.SOLD_OUT))
                .totalSoldQuantity(stockRepository.sumSoldQuantity())
                .build();
    }

    @Transactional(readOnly = true)
    public AdminEventDashboardResponse getEventDashboard(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));

        List<EventProductEntity> eventProducts = eventProductRepository.findByEventId(eventId);

        List<Long> eventProductIds = eventProducts.stream()
                .map(EventProductEntity::getId)
                .toList();

        long totalSales = 0;
        long totalSoldQuantity = 0;

        if (!eventProductIds.isEmpty()) {
            totalSales = orderItemRepository.sumTotalPriceByEventProductIdsAndOrderStatus(
                    eventProductIds,
                    OrderStatus.PAID
            );

            totalSoldQuantity = orderItemRepository.sumQuantityByEventProductIdsAndOrderStatus(
                    eventProductIds,
                    OrderStatus.PAID
            );
        }

        return AdminEventDashboardResponse.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventProductCount(eventProducts.size())
                .totalSales(totalSales)
                .totalSoldQuantity(totalSoldQuantity)
                .build();
    }

    @Transactional(readOnly = true)
    public AdminSalesDashboardResponse getSalesDashboard() {
        long totalSales = orderRepository.sumTotalAmountByStatus(OrderStatus.PAID);
        long paidOrders = orderRepository.countByStatus(OrderStatus.PAID);

        long averageOrderAmount = 0;
        if (paidOrders > 0) {
            averageOrderAmount = totalSales / paidOrders;
        }

        return AdminSalesDashboardResponse.builder()
                .totalSales(totalSales)
                .paidOrders(paidOrders)
                .averageOrderAmount(averageOrderAmount)
                .build();
    }
}