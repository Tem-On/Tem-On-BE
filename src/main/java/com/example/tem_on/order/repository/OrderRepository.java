package com.example.tem_on.order.repository;

import com.example.tem_on.order.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserIdOrderByOrderedAtDesc(Long userId);
}