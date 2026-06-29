package com.example.temon.orderpaymentservice.dashboard.service;


import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminDashboardResponse;
import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminEventDashboardResponse;
import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminSalesDashboardResponse;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import com.example.temon.orderpaymentservice.order.repository.OrderItemRepository;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;
import com.example.temon.orderpaymentservice.global.client.CommerceServiceClient;
import com.example.temon.orderpaymentservice.global.client.EventProductResponse;
import com.example.temon.orderpaymentservice.global.client.EventResponse;
import com.example.temon.orderpaymentservice.global.client.QueueStockServiceClient;
import com.example.temon.orderpaymentservice.global.client.StockResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final CommerceServiceClient commerceServiceClient;
    private final QueueStockServiceClient queueStockServiceClient;


    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        long totalOrders = orderRepository.count();
        long paidOrders = orderRepository.countByStatus(OrderStatus.PAID);
        long canceledOrders = orderRepository.countByStatus(OrderStatus.CANCELED);
        long totalSales = orderRepository.sumTotalAmountByStatus(OrderStatus.PAID);
        long activeEvents = commerceServiceClient.getOpenEvents().size();

        List<StockResponse> stocks = queueStockServiceClient.getStockList();
        
        long soldOutEventProducts = stocks.stream()
                .filter(stock -> stock.quantity() != null && stock.quantity() <= 0)
                .count();

        long totalSoldQuantity = stocks.stream()
                .mapToLong(stock -> stock.soldQuantity() != null ? stock.soldQuantity() : 0L)
                .sum();

        return AdminDashboardResponse.builder()
                .totalOrders(totalOrders)
                .paidOrders(paidOrders)
                .canceledOrders(canceledOrders)
                .totalSales(totalSales)
                .activeEvents(activeEvents)
                .soldOutEventProducts(soldOutEventProducts)
                .totalSoldQuantity(totalSoldQuantity)
                .build();
    }

    @Transactional(readOnly = true)
    public AdminEventDashboardResponse getEventDashboard(Long eventId) {
        EventResponse event = commerceServiceClient.getEventDetail(eventId);
        List<EventProductResponse> eventProducts = commerceServiceClient.getProductsByEventId(eventId);

        List<Long> eventProductIds = eventProducts.stream()
                .map(EventProductResponse::id) 
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
                .eventId(event.id())
                .eventTitle(event.title())
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