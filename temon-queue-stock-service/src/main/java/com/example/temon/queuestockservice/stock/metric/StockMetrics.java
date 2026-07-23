package com.example.temon.queuestockservice.stock.metric;

import com.example.temon.queuestockservice.stock.domain.entity.StockEntity;
import com.example.temon.queuestockservice.stock.repository.StockRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class StockMetrics {

    private final MeterRegistry meterRegistry;
    private final StockRepository stockRepository;

    private final ConcurrentMap<Long, AtomicLong> totalStockMap =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, AtomicLong> remainingStockMap =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, AtomicLong> reservedStockMap =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, AtomicLong> soldStockMap =
            new ConcurrentHashMap<>();


    @PostConstruct
    public void initializeStockMetrics() {
        stockRepository.findAll()
                .forEach(this::update);
    }


    public void update(StockEntity stock) {
        if (stock == null || stock.getEventProductId() == null) {
            return;
        }

        update(
                stock.getEventProductId(),
                stock.getTotalQuantity(),
                stock.getRemainingQuantity(),
                stock.getReservedQuantity(),
                stock.getSoldQuantity()
        );
    }


    public void update(
            Long eventProductId,
            long remainingQuantity,
            long reservedQuantity,
            long soldQuantity
    ) {
        if (eventProductId == null) {
            return;
        }

        getRemainingGauge(eventProductId)
                .set(remainingQuantity);

        getReservedGauge(eventProductId)
                .set(reservedQuantity);

        getSoldGauge(eventProductId)
                .set(soldQuantity);


        getTotalGauge(eventProductId)
                .set(
                        remainingQuantity
                                + reservedQuantity
                                + soldQuantity
                );
    }

    public void update(
            Long eventProductId,
            long totalQuantity,
            long remainingQuantity,
            long reservedQuantity,
            long soldQuantity
    ) {
        if (eventProductId == null) {
            return;
        }

        getTotalGauge(eventProductId)
                .set(totalQuantity);

        getRemainingGauge(eventProductId)
                .set(remainingQuantity);

        getReservedGauge(eventProductId)
                .set(reservedQuantity);

        getSoldGauge(eventProductId)
                .set(soldQuantity);
    }

    private AtomicLong getTotalGauge(Long eventProductId) {
        return totalStockMap.computeIfAbsent(
                eventProductId,
                id -> {
                    AtomicLong value = new AtomicLong(0L);

                    Gauge.builder(
                                    "temon.stock.total",
                                    value,
                                    AtomicLong::get
                            )
                            .description(
                                    "Current total stock quantity"
                            )
                            .tag(
                                    "event_product_id",
                                    String.valueOf(id)
                            )
                            .register(meterRegistry);

                    return value;
                }
        );
    }

    private AtomicLong getRemainingGauge(Long eventProductId) {
        return remainingStockMap.computeIfAbsent(
                eventProductId,
                id -> {
                    AtomicLong value = new AtomicLong(0L);

                    Gauge.builder(
                                    "temon.stock.remaining",
                                    value,
                                    AtomicLong::get
                            )
                            .description(
                                    "Current remaining stock quantity"
                            )
                            .tag(
                                    "event_product_id",
                                    String.valueOf(id)
                            )
                            .register(meterRegistry);

                    return value;
                }
        );
    }

    private AtomicLong getReservedGauge(Long eventProductId) {
        return reservedStockMap.computeIfAbsent(
                eventProductId,
                id -> {
                    AtomicLong value = new AtomicLong(0L);

                    Gauge.builder(
                                    "temon.stock.reserved",
                                    value,
                                    AtomicLong::get
                            )
                            .description(
                                    "Current reserved stock quantity"
                            )
                            .tag(
                                    "event_product_id",
                                    String.valueOf(id)
                            )
                            .register(meterRegistry);

                    return value;
                }
        );
    }

    private AtomicLong getSoldGauge(Long eventProductId) {
        return soldStockMap.computeIfAbsent(
                eventProductId,
                id -> {
                    AtomicLong value = new AtomicLong(0L);

                    Gauge.builder(
                                    "temon.stock.sold",
                                    value,
                                    AtomicLong::get
                            )
                            .description(
                                    "Current sold stock quantity"
                            )
                            .tag(
                                    "event_product_id",
                                    String.valueOf(id)
                            )
                            .register(meterRegistry);

                    return value;
                }
        );
    }
}