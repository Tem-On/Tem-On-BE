package com.example.tem_on.dashboard.service;

import com.example.tem_on.dashboard.domain.dto.AdminDashboardResponse;
import com.example.tem_on.event.domain.entity.EventProductStatus;
import com.example.tem_on.event.domain.entity.EventStatus;
import com.example.tem_on.event.repository.EventProductRepository;
import com.example.tem_on.event.repository.EventRepository;
import com.example.tem_on.order.domain.entity.OrderStatus;
import com.example.tem_on.order.repository.OrderRepository;
import com.example.tem_on.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderRepository orderRepository;
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
}