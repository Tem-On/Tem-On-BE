package com.example.temon.queuestockservice.stock.service;

import com.example.temon.common.dto.event.StockChangedEvent;
import com.example.temon.queuestockservice.global.kafka.producer.KafkaEventProducer;
import com.example.temon.queuestockservice.stock.domain.entity.StockEntity;
import com.example.temon.queuestockservice.stock.metric.StockMetrics;
import com.example.temon.queuestockservice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final StockMetrics stockMetrics;

    @Transactional(readOnly = true)
    public StockEntity getStock(Long eventProductId) {
        return stockRepository.findByEventProductId(eventProductId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "해당 상품의 재고 정보가 존재하지 않습니다."
                        )
                );
    }

    @Transactional(readOnly = true)
    public boolean isSoldOut(Long eventProductId) {
        StockEntity stock = getStock(eventProductId);

        return stock.getRemainingQuantity() <= 0;
    }


    @Transactional
    public void reserveStock(
            Long eventProductId,
            int quantity
    ) {
        validateQuantity(quantity);

        StockEntity stock = getStock(eventProductId);

        stock.reserve(quantity);

        updateMetricsAndPublish(stock);
    }

    @Transactional
    public void releaseStock(
            Long eventProductId,
            int quantity
    ) {
        validateQuantity(quantity);

        StockEntity stock = getStock(eventProductId);

        stock.release(quantity);

        updateMetricsAndPublish(stock);
    }


    @Transactional
    public void confirmStock(
            Long eventProductId,
            int quantity
    ) {
        validateQuantity(quantity);

        StockEntity stock = getStock(eventProductId);

        stock.confirm(quantity);

        updateMetricsAndPublish(stock);
    }


    @Transactional
    public void cancelSoldStock(
            Long eventProductId,
            int quantity
    ) {
        validateQuantity(quantity);

        StockEntity stock = getStock(eventProductId);

        stock.cancelSold(quantity);

        updateMetricsAndPublish(stock);
    }


    private void updateMetricsAndPublish(
            StockEntity stock
    ) {
        stockMetrics.update(stock);

        publishStockChanged(stock);
    }

    private void publishStockChanged(
            StockEntity stock
    ) {
        StockChangedEvent event = StockChangedEvent.builder()
                .eventProductId(
                        stock.getEventProductId()
                )
                .remainingQuantity(
                        stock.getRemainingQuantity()
                )
                .reservedQuantity(
                        stock.getReservedQuantity()
                )
                .soldQuantity(
                        stock.getSoldQuantity()
                )
                .build();

        kafkaEventProducer.publish(
                "stock-changed",
                event
        );
    }

    @Transactional(readOnly = true)
    public List<StockEntity> getStocksByEventProductIds(
            List<Long> eventProductIds
    ) {
        if (eventProductIds == null
                || eventProductIds.isEmpty()) {

            return List.of();
        }

        return stockRepository.findByEventProductIdIn(
                eventProductIds
        );
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "재고 수량은 1개 이상이어야 합니다."
            );
        }
    }
}