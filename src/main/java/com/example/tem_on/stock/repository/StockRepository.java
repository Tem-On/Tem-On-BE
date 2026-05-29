package com.example.tem_on.stock.repository;


import com.example.tem_on.stock.domain.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Long> {
    
    Optional<StockEntity> findByEventProductId(Long eventProductId);
}