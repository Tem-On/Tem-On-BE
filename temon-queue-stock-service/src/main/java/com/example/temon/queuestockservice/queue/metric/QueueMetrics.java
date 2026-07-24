package com.example.temon.queuestockservice.queue.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class QueueMetrics {

    private final MeterRegistry meterRegistry;


    private final ConcurrentMap<Long, AtomicLong> waitingCountMap =
            new ConcurrentHashMap<>();


    private final ConcurrentMap<Long, Counter> enterCounterMap =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, Counter> admittedCounterMap =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, Counter> completedCounterMap =
            new ConcurrentHashMap<>();

    public QueueMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }


    public void updateWaitingCount(
            Long eventProductId,
            long waitingCount
    ) {
        AtomicLong gaugeValue = waitingCountMap.computeIfAbsent(
                eventProductId,
                id -> registerWaitingGauge(id)
        );

        gaugeValue.set(waitingCount);
    }


    public void incrementEnter(
            Long eventProductId
    ) {
        getEnterCounter(eventProductId).increment();
    }

    public void incrementEnter(
            Long eventProductId,
            long count
    ) {
        if (count > 0) {
            getEnterCounter(eventProductId).increment(count);
        }
    }


    public void incrementAdmitted(
            Long eventProductId
    ) {
        getAdmittedCounter(eventProductId).increment();
    }

    public void incrementAdmitted(
            Long eventProductId,
            long count
    ) {
        if (count > 0) {
            getAdmittedCounter(eventProductId).increment(count);
        }
    }


    public void incrementCompleted(
            Long eventProductId
    ) {
        getCompletedCounter(eventProductId).increment();
    }

    private AtomicLong registerWaitingGauge(
            Long eventProductId
    ) {
        AtomicLong value = new AtomicLong(0L);

        Gauge.builder(
                        "temon.queue.waiting.count",
                        value,
                        AtomicLong::get
                )
                .description("Current number of users waiting in queue")
                .tag(
                        "event_product_id",
                        String.valueOf(eventProductId)
                )
                .register(meterRegistry);

        return value;
    }

    private Counter getEnterCounter(
            Long eventProductId
    ) {
        return enterCounterMap.computeIfAbsent(
                eventProductId,
                id -> Counter.builder("temon.queue.enter")
                        .description(
                                "Total number of users newly entered into queue"
                        )
                        .tag(
                                "event_product_id",
                                String.valueOf(id)
                        )
                        .register(meterRegistry)
        );
    }

    private Counter getAdmittedCounter(
            Long eventProductId
    ) {
        return admittedCounterMap.computeIfAbsent(
                eventProductId,
                id -> Counter.builder("temon.queue.admitted")
                        .description(
                                "Total number of users admitted from queue"
                        )
                        .tag(
                                "event_product_id",
                                String.valueOf(id)
                        )
                        .register(meterRegistry)
        );
    }

    private Counter getCompletedCounter(
            Long eventProductId
    ) {
        return completedCounterMap.computeIfAbsent(
                eventProductId,
                id -> Counter.builder("temon.queue.completed")
                        .description(
                                "Total number of admitted users completed"
                        )
                        .tag(
                                "event_product_id",
                                String.valueOf(id)
                        )
                        .register(meterRegistry)
        );
    }
}