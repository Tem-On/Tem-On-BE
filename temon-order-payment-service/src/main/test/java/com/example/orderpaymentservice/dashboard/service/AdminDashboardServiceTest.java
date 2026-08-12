package com.example.temon.orderpaymentservice.dashboard.service;

import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminDashboardResponse;
import com.example.temon.orderpaymentservice.dashboard.domain.dto.AdminEventDashboardResponse;
import com.example.temon.orderpaymentservice.global.client.CommerceServiceClient;
import com.example.temon.orderpaymentservice.global.client.EventProductResponse;
import com.example.temon.orderpaymentservice.global.client.EventResponse;
import com.example.temon.orderpaymentservice.global.client.QueueStockServiceClient;
import com.example.temon.orderpaymentservice.global.client.StockResponse;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import com.example.temon.orderpaymentservice.order.repository.OrderItemRepository;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @InjectMocks
    private AdminDashboardService adminDashboardService; 

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private CommerceServiceClient commerceServiceClient;
    @Mock private QueueStockServiceClient queueStockServiceClient;

    @Test
    void 메인_대시보드_조회_단위테스트() {
        Mockito.when(orderRepository.count()).thenReturn(10L); 
        Mockito.when(orderRepository.countByStatus(OrderStatus.PAID)).thenReturn(7L); 
        Mockito.when(orderRepository.countByStatus(OrderStatus.CANCELED)).thenReturn(3L); 
        Mockito.when(orderRepository.sumTotalAmountByStatus(OrderStatus.PAID)).thenReturn(150000L);

        Mockito.when(commerceServiceClient.getOpenEvents()).thenReturn(List.of(
                new EventResponse(1L, "선착순 100명 특가", "OPEN"),
                new EventResponse(2L, "타임세일 이벤트", "OPEN")
        ));

        Mockito.when(queueStockServiceClient.getStockList()).thenReturn(List.of(
                new StockResponse(101L, 50, 20),  
                new StockResponse(102L, 0, 35)    
        ));

        AdminDashboardResponse response = adminDashboardService.getDashboard();

        assertNotNull(response);
        assertEquals(10L, response.getTotalOrders());
        assertEquals(2L, response.getActiveEvents()); 
        assertEquals(1L, response.getSoldOutEventProducts());
        assertEquals(55L, response.getTotalSoldQuantity()); 

        System.out.println("=========================================");
        System.out.println("✔ 메인 대시보드 테스트 성공!");
        System.out.println("✔ 품절 상품 수: " + response.getSoldOutEventProducts());
        System.out.println("✔ 총 누적 판매량: " + response.getTotalSoldQuantity());
        System.out.println("=========================================");
    }

    @Test
    void 이벤트별_대시보드_조회_단위테스트() {
        Long eventId = 1L;
        Mockito.when(commerceServiceClient.getEventDetail(eventId))
                .thenReturn(new EventResponse(eventId, "초특가 대박 이벤트", "OPEN"));

        Mockito.when(commerceServiceClient.getProductsByEventId(eventId)).thenReturn(List.of(
                new EventProductResponse(101L, 501L, 10000, 100),
                new EventProductResponse(102L, 502L, 20000, 50)
        ));

        List<Long> targetProductIds = List.of(101L, 102L);
        Mockito.when(orderItemRepository.sumTotalPriceByEventProductIdsAndOrderStatus(targetProductIds, OrderStatus.PAID))
                .thenReturn(70000L); 
        Mockito.when(orderItemRepository.sumQuantityByEventProductIdsAndOrderStatus(targetProductIds, OrderStatus.PAID))
                .thenReturn(5L); 

        AdminEventDashboardResponse response = adminDashboardService.getEventDashboard(eventId);

        assertNotNull(response);
        assertEquals("초특가 대박 이벤트", response.getEventTitle());
        assertEquals(2, response.getEventProductCount());
        assertEquals(70000L, response.getTotalSales());
        assertEquals(5L, response.getTotalSoldQuantity());

        System.out.println("=========================================");
        System.out.println("✔ 이벤트별 대시보드 테스트 성공!");
        System.out.println("✔ 이벤트 타이틀: " + response.getEventTitle());
        System.out.println("✔ 이벤트 내 상품 개수: " + response.getEventProductCount());
        System.out.println("=========================================");
    }
}