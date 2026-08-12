package com.example.temon.orderpaymentservice.order.facade;

import com.example.temon.orderpaymentservice.global.client.CommerceServiceClient;
import com.example.temon.orderpaymentservice.global.client.EventProductResponse;
import com.example.temon.orderpaymentservice.global.client.ProductResponse;
import com.example.temon.orderpaymentservice.global.client.QueueStockServiceClient;
import com.example.temon.orderpaymentservice.global.client.StockRequest;
import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderNotification;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderCreateItemRequest;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderCreateRequest;
import com.example.temon.orderpaymentservice.order.domain.dto.OrderResponse;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderEntity;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderItemEntity;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import com.example.temon.orderpaymentservice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CommerceServiceClient commerceServiceClient;
    private final QueueStockServiceClient queueStockServiceClient;

    @Transactional
    public OrderResponse createOrder(
            Long userId,
            OrderCreateRequest request
    ) {
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .orderNumber(UUID.randomUUID().toString())
                .totalAmount(0)
                .status(OrderStatus.CREATED)
                .orderedAt(LocalDateTime.now())
                .build();

        int totalAmount = 0;

        List<Long> orderedEventProductIds =
                new ArrayList<>();

        for (OrderCreateItemRequest itemRequest : request.getItems()) {
            EventProductResponse eventProduct =
                    commerceServiceClient.getEventProduct(
                            itemRequest.getEventProductId()
                    );

            ProductResponse product =
                    commerceServiceClient.getProduct(
                            eventProduct.productId()
                    );

            queueStockServiceClient.validatePurchaseAccess(
                    eventProduct.id(),
                    userId
            );

            queueStockServiceClient.reserveStock(
                    new StockRequest(
                            eventProduct.id(),
                            itemRequest.getQuantity()
                    )
            );

            int orderPrice = eventProduct.eventPrice();

            int totalPrice =
                    orderPrice * itemRequest.getQuantity();

            OrderItemEntity orderItem =
                    OrderItemEntity.builder()
                            .eventProductId(eventProduct.id())
                            .productName(product.name())
                            .quantity(itemRequest.getQuantity())
                            .orderPrice(orderPrice)
                            .totalPrice(totalPrice)
                            .build();

            order.addOrderItem(orderItem);

            totalAmount += totalPrice;

            orderedEventProductIds.add(
                    eventProduct.id()
            );
        }

        order.updateTotalAmount(totalAmount);

        OrderEntity savedOrder =
                orderRepository.save(order);

        messagingTemplate.convertAndSend(
                "/topic/admin/orders",
                AdminOrderNotification.from(savedOrder)
        );

        for (Long eventProductId : orderedEventProductIds) {
            queueStockServiceClient.completeQueue(
                    eventProductId
            );
        }

        /*
         * 주문 생성 시점에는 결제 요청이 아직 생성되지 않았으므로
         * paymentId는 null입니다.
         */
        return OrderResponse.from(savedOrder, null);
    }

    @Transactional
    public void cancelOrder(
            Long userId,
            Long orderId
    ) {
        OrderEntity order =
                orderRepository.findById(orderId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "주문을 찾을 수 없습니다."
                                )
                        );

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "본인의 주문만 취소할 수 있습니다."
            );
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException(
                    "이미 취소된 주문입니다."
            );
        }

        for (OrderItemEntity item : order.getOrderItems()) {
            queueStockServiceClient.releaseStock(
                    new StockRequest(
                            item.getEventProductId(),
                            item.getQuantity()
                    )
            );
        }

        order.cancel();
    }
}