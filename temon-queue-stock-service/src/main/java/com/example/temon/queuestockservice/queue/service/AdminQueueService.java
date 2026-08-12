package com.example.temon.queuestockservice.queue.service;

import com.example.temon.queuestockservice.global.client.CommerceEventProductClient;
import com.example.temon.queuestockservice.global.client.EventProductClientResponse;
import com.example.temon.queuestockservice.queue.domain.dto.AdminQueueResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueRealtimeResponse;
import com.example.temon.queuestockservice.queue.redis.QueueRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final CommerceEventProductClient commerceEventProductClient;

    private static final String OPEN = "OPEN";
    private static final String CLOSED = "CLOSED";

    /**
     * 관리자 화면에 표시할 전체 대기열 목록
     */
    public List<AdminQueueResponse> getQueues() {

        List<EventProductClientResponse> eventProducts =
                commerceEventProductClient.getAllEventProducts();

        return eventProducts.stream()
                .map(this::createAdminQueueResponse)
                .toList();
    }

    /**
     * 이벤트 상품 하나의 대기열 조회
     */
    public AdminQueueResponse getQueue(Long eventProductId) {

        EventProductClientResponse eventProduct =
                commerceEventProductClient.getEventProduct(eventProductId);

        return createAdminQueueResponse(eventProduct);
    }

    private AdminQueueResponse createAdminQueueResponse(
            EventProductClientResponse eventProduct
    ) {
        Long eventProductId = eventProduct.id();

        Long waitingCount = redisTemplate.opsForZSet()
                .size(QueueRedisKey.waitingQueueKey(eventProductId));

        String gateStatus = redisTemplate.opsForValue()
                .get(QueueRedisKey.statusKey(eventProductId));

        String enteredCountValue = redisTemplate.opsForValue()
                .get(QueueRedisKey.enteredCountKey(eventProductId));

        if (gateStatus == null) {
            gateStatus = OPEN;
        }

        Long enteredCount = enteredCountValue == null
                ? 0L
                : Long.parseLong(enteredCountValue);

        return new AdminQueueResponse(
                eventProductId,
                eventProduct.eventId(),
                eventProduct.eventTitle(),
                eventProduct.productName(),
                gateStatus,
                waitingCount == null ? 0L : waitingCount,
                enteredCount
        );
    }

    public AdminQueueResponse openQueue(Long eventProductId) {

        redisTemplate.opsForValue().set(
                QueueRedisKey.statusKey(eventProductId),
                OPEN
        );

        publishQueueMessage(
                eventProductId,
                "OPEN",
                "관리자가 대기열을 열었습니다."
        );

        return getQueue(eventProductId);
    }

    public AdminQueueResponse closeQueue(Long eventProductId) {

        redisTemplate.opsForValue().set(
                QueueRedisKey.statusKey(eventProductId),
                CLOSED
        );

        publishQueueMessage(
                eventProductId,
                "CLOSE",
                "관리자가 대기열을 종료했습니다."
        );

        return getQueue(eventProductId);
    }

    public void clearQueue(Long eventProductId) {

        redisTemplate.delete(
                QueueRedisKey.waitingQueueKey(eventProductId)
        );

        redisTemplate.delete(
                QueueRedisKey.statusKey(eventProductId)
        );

        redisTemplate.delete(
                QueueRedisKey.enteredCountKey(eventProductId)
        );

        Set<String> availableKeys =
                redisTemplate.keys(
                        QueueRedisKey.availableKeyPattern(eventProductId)
                );

        if (availableKeys != null && !availableKeys.isEmpty()) {
            redisTemplate.delete(availableKeys);
        }

        publishQueueMessage(
                eventProductId,
                "RESET",
                "관리자가 대기열을 초기화했습니다."
        );
    }

    private void publishQueueMessage(
            Long eventProductId,
            String type,
            String message
    ) {
        Long currentUsers =
                redisTemplate.opsForZSet().size(
                        QueueRedisKey.waitingQueueKey(eventProductId)
                );

        messagingTemplate.convertAndSend(
                "/topic/queue/" + eventProductId,
                new QueueRealtimeResponse(
                        eventProductId,
                        currentUsers == null ? 0L : currentUsers,
                        type,
                        message
                )
        );
    }
}