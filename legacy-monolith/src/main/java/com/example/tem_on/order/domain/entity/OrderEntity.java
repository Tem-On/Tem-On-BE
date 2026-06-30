package com.example.tem_on.order.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    private LocalDateTime canceledAt;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItemEntity orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void updateTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;

        if (status == OrderStatus.CANCELED) {
            this.canceledAt = LocalDateTime.now();
        }
    }
}