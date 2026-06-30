package com.example.tem_on.queue.service;

import com.example.tem_on.event.repository.EventProductRepository;
import com.example.tem_on.queue.domain.dto.AdminQueueResponse;
import com.example.tem_on.queue.domain.dto.QueueRealtimeResponse;
import com.example.tem_on.queue.redis.QueueRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EventProductRepository eventProductRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String OPEN = "OPEN";
    private static final String CLOSED = "CLOSED";

    public AdminQueueResponse getQueue(Long eventProductId) {
        validateEventProductExists(eventProductId);

        String queueKey = QueueRedisKey.waitingQueueKey(eventProductId);
        String statusKey = QueueRedisKey.statusKey(eventProductId);

        Long waitingUsers = redisTemplate.opsForZSet().size(queueKey);
        String status = redisTemplate.opsForValue().get(statusKey);

        if (status == null) {
            status = OPEN;
        }

        return new AdminQueueResponse(
                eventProductId,
                status,
                waitingUsers == null ? 0L : waitingUsers
        );
    }

    public AdminQueueResponse openQueue(Long eventProductId) {
        validateEventProductExists(eventProductId);

        redisTemplate.opsForValue().set(
                QueueRedisKey.statusKey(eventProductId),
                OPEN
        );

        publishQueueMessage(eventProductId, "관리자가 대기열을 열었습니다.");

        return getQueue(eventProductId);
    }

    public AdminQueueResponse closeQueue(Long eventProductId) {
        validateEventProductExists(eventProductId);

        redisTemplate.opsForValue().set(
                QueueRedisKey.statusKey(eventProductId),
                CLOSED
        );

        publishQueueMessage(eventProductId, "관리자가 대기열을 종료했습니다.");

        return getQueue(eventProductId);
    }

    public void clearQueue(Long eventProductId) {
        validateEventProductExists(eventProductId);

        redisTemplate.delete(QueueRedisKey.waitingQueueKey(eventProductId));
        redisTemplate.delete(QueueRedisKey.statusKey(eventProductId));

        Set<String> availableKeys = redisTemplate.keys(
                QueueRedisKey.availableKeyPattern(eventProductId)
        );

        if (availableKeys != null && !availableKeys.isEmpty()) {
            redisTemplate.delete(availableKeys);
        }

        publishQueueMessage(eventProductId, "관리자가 대기열을 초기화했습니다.");
    }

    private void validateEventProductExists(Long eventProductId) {
        eventProductRepository.findById(eventProductId)
                .orElseThrow(() -> new RuntimeException("이벤트 상품이 존재하지 않습니다."));
    }

    private void publishQueueMessage(Long eventProductId, String message) {
        Long currentUsers = redisTemplate.opsForZSet()
                .size(QueueRedisKey.waitingQueueKey(eventProductId));

        messagingTemplate.convertAndSend(
                "/topic/queue/" + eventProductId,
                new QueueRealtimeResponse(
                        eventProductId,
                        currentUsers == null ? 0L : currentUsers,
                        message
                )
        );
    }
}