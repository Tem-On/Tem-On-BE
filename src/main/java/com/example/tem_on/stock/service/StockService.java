package com.example.tem_on.stock.service;

import com.example.tem_on.global.kafka.event.StockChangedEvent;
import com.example.tem_on.global.kafka.producer.KafkaEventProducer;
import com.example.tem_on.stock.domain.entity.StockEntity;
import com.example.tem_on.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final KafkaEventProducer kafkaEventProducer;

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
        publishStockChanged(stock);
    }

    @Transactional
    public void releaseStock(Long eventProductId, int quantity) {
        StockEntity stock = getStock(eventProductId);
        stock.release(quantity);
        publishStockChanged(stock);
    }

    @Transactional
    public void confirmStock(Long eventProductId, int quantity) {
        StockEntity stock = getStock(eventProductId);
        stock.confirm(quantity);
        publishStockChanged(stock);
    }

    @Transactional
    public void cancelSoldStock(Long eventProductId, int quantity) {
        StockEntity stock = getStock(eventProductId);
        stock.cancelSold(quantity);
        publishStockChanged(stock);
    }

    private void publishStockChanged(StockEntity stock) {
        StockChangedEvent event = StockChangedEvent.builder()
                .eventProductId(stock.getEventProductId())
                .remainingQuantity(stock.getRemainingQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .soldQuantity(stock.getSoldQuantity())
                .build();

        kafkaEventProducer.publish("stock-changed", event);
    }
}