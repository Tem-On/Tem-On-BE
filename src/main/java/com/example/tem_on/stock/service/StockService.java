package com.example.tem_on.stock.service;

import com.example.tem_on.stock.domain.entity.StockEntity;
import com.example.tem_on.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public StockEntity getStock(Long eventProductId) {
        return stockRepository.findByEventProductId(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품의 재고 정보가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public boolean isSoldOut(Long eventProductId) {
        StockEntity stock = getStock(eventProductId);
        return stock.getRemainingQuantity() <= 0;
    }

    @Transactional
    public void reserveStock(Long eventProductId, int quantity) {
        StockEntity stock = getStock(eventProductId);
        stock.reserve(quantity);
    }

    @Transactional
    public void releaseStock(Long eventProductId, int quantity) {
        StockEntity stock = getStock(eventProductId);
        stock.release(quantity);
    }

    @Transactional
    public void confirmStock(Long eventProductId, int quantity) {
        StockEntity stock = getStock(eventProductId);
        stock.confirm(quantity);
    }

    @Transactional
    public void cancelSoldStock(Long eventProductId, int quantity) {
        StockEntity stock = getStock(eventProductId);
        stock.cancelSold(quantity);
    }
}