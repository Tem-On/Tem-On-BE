package com.example.tem_on.order.repository;

import com.example.tem_on.order.domain.entity.OrderEntity;
import com.example.tem_on.order.domain.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserIdOrderByOrderedAtDesc(Long userId);

    Page<OrderEntity> findAllByOrderByOrderedAtDesc(Pageable pageable);

    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from OrderEntity o where o.status = :status")
    long sumTotalAmountByStatus(@Param("status") OrderStatus status);
}