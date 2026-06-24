package com.example.temon.queuestockservice.stock.service;

import com.example.temon.queuestockservice.stock.domain.dto.StockRequest;
import com.example.temon.queuestockservice.stock.domain.dto.StockResponse;
import com.example.temon.queuestockservice.stock.domain.entity.StockEntity;
import com.example.temon.queuestockservice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void createStock(StockRequest request) {
        stockRepository.findByEventProductId(request.getEventProductId())
                .ifPresent(s -> {
                    throw new IllegalArgumentException("이미 재고가 등록된 이벤트 상품입니다. ID: " + request.getEventProductId());
                });

        StockEntity stock = StockEntity.builder()
                .eventProductId(request.getEventProductId())
                .totalQuantity(request.getQuantity())
                .remainingQuantity(request.getQuantity()) 
                .reservedQuantity(0)
                .soldQuantity(0)
                .build();

        stockRepository.save(stock);
    }

    public List<StockResponse> getStockList() {
        return stockRepository.findAll().stream()
                .map(StockResponse::new)
                .collect(Collectors.toList());
    }

    public StockResponse getStockDetail(Long eventProductId) {
        StockEntity stock = stockRepository.findByEventProductId(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("재고 정보가 존재하지 않습니다. 상품 ID: " + eventProductId));
        return new StockResponse(stock);
    }

    @Transactional
    public void updateStockQuantity(Long eventProductId, int newQuantity) { 
        StockEntity stock = stockRepository.findByEventProductId(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("재고 정보가 존재하지 않습니다. 상품 ID: " + eventProductId));

        stock.updateQuantity(newQuantity);
    }

    @Transactional
    public void forceSoldOut(Long eventProductId) {
        StockEntity stock = stockRepository.findByEventProductId(eventProductId)
                .orElseThrow(() -> new IllegalArgumentException("재고 정보가 존재하지 않습니다. 상품 ID: " + eventProductId));

        stock.forceSoldOut();
    }
}