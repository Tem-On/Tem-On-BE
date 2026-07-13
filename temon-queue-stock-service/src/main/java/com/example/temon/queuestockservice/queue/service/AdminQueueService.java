package com.example.temon.queuestockservice.queue.service;

import com.example.temon.queuestockservice.queue.domain.dto.AdminQueueResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueRealtimeResponse;
import com.example.temon.queuestockservice.queue.redis.QueueRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String OPEN = "OPEN";
    private static final String CLOSED = "CLOSED";

    public AdminQueueResponse getQueue(Long eventProductId) {
        String queueKey =
                QueueRedisKey.waitingQueueKey(eventProductId);

        String statusKey =
                QueueRedisKey.statusKey(eventProductId);

        Long waitingUsers =
                redisTemplate.opsForZSet().size(queueKey);

        String status =
                redisTemplate.opsForValue().get(statusKey);

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