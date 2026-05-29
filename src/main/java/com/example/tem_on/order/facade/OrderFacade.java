package com.example.tem_on.order.facade;

import com.example.tem_on.event.domain.entity.EventProductEntity;
import com.example.tem_on.event.repository.EventProductRepository;
import com.example.tem_on.order.domain.dto.OrderCreateItemRequest;
import com.example.tem_on.order.domain.dto.OrderCreateRequest;
import com.example.tem_on.order.domain.dto.OrderResponse;
import com.example.tem_on.order.domain.entity.OrderEntity;
import com.example.tem_on.order.domain.entity.OrderItemEntity;
import com.example.tem_on.order.domain.entity.OrderStatus;
import com.example.tem_on.order.repository.OrderRepository;
import com.example.tem_on.product.domain.entity.Product;
import com.example.tem_on.product.repository.ProductRepository;
import com.example.tem_on.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderRepository orderRepository;
    private final StockService stockService;
    private final EventProductRepository eventProductRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {

        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .orderNumber(UUID.randomUUID().toString())
                .totalAmount(0)
                .status(OrderStatus.CREATED)
                .orderedAt(LocalDateTime.now())
                .build();

        int totalAmount = 0;

        for (OrderCreateItemRequest itemRequest : request.getItems()) {

            EventProductEntity eventProduct = eventProductRepository.findById(itemRequest.getEventProductId())
                    .orElseThrow(() -> new IllegalArgumentException("이벤트 상품을 찾을 수 없습니다."));

            Product product = productRepository.findById(eventProduct.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            stockService.reserveStock(
                    eventProduct.getId(),
                    itemRequest.getQuantity()
            );

            int orderPrice = eventProduct.getEventPrice();
            int totalPrice = orderPrice * itemRequest.getQuantity();

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .eventProductId(eventProduct.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .orderPrice(orderPrice)
                    .totalPrice(totalPrice)
                    .build();

            order.addOrderItem(orderItem);
            totalAmount += totalPrice;
        }

        order.updateTotalAmount(totalAmount);

        OrderEntity savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다.");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        for (OrderItemEntity item : order.getOrderItems()) {
            stockService.releaseStock(
                    item.getEventProductId(),
                    item.getQuantity()
            );
        }

        order.cancel();
    }
}