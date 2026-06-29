package com.example.temon.orderpaymentservice.order.facade;

import com.example.temon.orderpaymentservice.global.client.CommerceServiceClient;
import com.example.temon.orderpaymentservice.global.client.QueueStockServiceClient;
import com.example.temon.orderpaymentservice.global.client.dto.EventProductResponse;
import com.example.temon.orderpaymentservice.global.client.dto.ProductResponse;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderCreateItemRequest;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderCreateRequest;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderResponse;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;

    @Mock private OrderRepository orderRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private CommerceServiceClient commerceServiceClient;
    @Mock private QueueStockServiceClient queueStockServiceClient;

    @Test
    void 주문생성_비즈니스로직_단위테스트() {
        Long userId = 1L;
        OrderCreateRequest request = new OrderCreateRequest(
                List.of(new OrderCreateItemRequest(100L, 2))
        );

        Mockito.when(commerceServiceClient.getEventProduct(100L))
                .thenReturn(new EventProductResponse(100L, 500L, 15000, 10));
                
        Mockito.when(commerceServiceClient.getProduct(500L))
                .thenReturn(new ProductResponse(500L, "테스트 한정 특가 상품", 20000));
        
        Mockito.when(orderRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderFacade.createOrder(userId, request);

        assertNotNull(response);
        System.out.println(response.getTotalAmount()); 
 
    }
}