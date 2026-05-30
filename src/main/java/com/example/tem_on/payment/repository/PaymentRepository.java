package com.example.tem_on.payment.repository;

import com.example.tem_on.payment.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderId(Long orderId);
}