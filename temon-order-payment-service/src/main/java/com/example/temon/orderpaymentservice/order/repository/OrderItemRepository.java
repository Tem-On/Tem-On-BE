package com.example.temon.orderpaymentservice.order.repository;

import com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderEventProductStatisticsResponse;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderItemEntity;
import com.example.temon.orderpaymentservice.order.domain.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByOrderId(Long orderId);

    @Query("""
            select new com.example.temon.orderpaymentservice.order.domain.dto.AdminOrderEventProductStatisticsResponse(
                oi.eventProductId,
                oi.productName,
                count(distinct o.id),
                coalesce(sum(oi.quantity), 0),
                coalesce(sum(oi.totalPrice), 0)
            )
            from OrderItemEntity oi
            join oi.order o
            where o.status = :status
            group by oi.eventProductId, oi.productName
            order by sum(oi.totalPrice) desc
            """)
    List<AdminOrderEventProductStatisticsResponse> findEventProductStatisticsByOrderStatus(
            OrderStatus status
    );

    @Query("""
        select coalesce(sum(oi.totalPrice), 0)
        from OrderItemEntity oi
        join oi.order o
        where o.status = :status
        and oi.eventProductId in :eventProductIds
        """)
    long sumTotalPriceByEventProductIdsAndOrderStatus(
            @Param("eventProductIds") List<Long> eventProductIds,
            @Param("status") OrderStatus status
    );

    @Query("""
            select coalesce(sum(oi.quantity), 0)
            from OrderItemEntity oi
            join oi.order o
            where o.status = :status
            and oi.eventProductId in :eventProductIds
            """)
    long sumQuantityByEventProductIdsAndOrderStatus(
            @Param("eventProductIds") List<Long> eventProductIds,
            @Param("status") OrderStatus status
    );
}