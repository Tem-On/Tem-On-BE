package com.example.tem_on.order.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "event_product_id", nullable = false)
    private Long eventProductId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int totalPrice;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
}