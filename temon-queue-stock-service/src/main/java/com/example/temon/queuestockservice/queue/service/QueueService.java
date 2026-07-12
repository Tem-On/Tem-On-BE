package com.example.temon.queuestockservice.queue.service;

import com.example.temon.queuestockservice.queue.domain.dto.QueueAvailableResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueCurrentUsersResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueEnterResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueEstimatedTimeResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueRankResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueRealtimeResponse;
import com.example.temon.queuestockservice.queue.domain.dto.QueueStatusResponse;
import com.example.temon.queuestockservice.queue.redis.QueueRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.example.temon.queuestockservice.global.client.CommerceEventProductClient;
import com.example.temon.queuestockservice.global.client.EventProductValidationResponse;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int ALLOW_COUNT = 100;
    private static final long AVAILABLE_TTL_MINUTES = 10;
    private final CommerceEventProductClient commerceEventProductClient;

    public QueueEnterResponse enter(Long eventProductId, Long userId) {
        validateQueueAvailable(eventProductId);
        validateQueueIsOpen(eventProductId);

        String queueKey = QueueRedisKey.waitingQueueKey(eventProductId);

        Double score = redisTemplate.opsForZSet()
                .score(queueKey, String.valueOf(userId));

        if (score == null) {
            redisTemplate.opsForZSet()
                    .add(queueKey, String.valueOf(userId), System.currentTimeMillis());
        }

        Long rank = redisTemplate.opsForZSet()
                .rank(queueKey, String.valueOf(userId));

        if (rank == null) {
            throw new RuntimeException("대기열 순번 조회에 실패했습니다.");
        }

        if (rank == 0) {
            String availableKey = QueueRedisKey.availableKey(
                    eventProductId,
                    userId
            );

            redisTemplate.opsForValue().set(
                    availableKey,
                    "true",
                    AVAILABLE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );

            redisTemplate.opsForZSet()
                    .remove(queueKey, String.valueOf(userId));

            publishQueueRealtime(eventProductId, "첫 번째 사용자가 바로 입장했습니다.");

            return new QueueEnterResponse(
                    eventProductId,
                    userId,
                    0L,
                    "AVAILABLE"
            );
        }

        publishQueueRealtime(eventProductId, "대기열 인원이 변경되었습니다.");

        return new QueueEnterResponse(
                eventProductId,
                userId,
                rank + 1,
                "WAITING"
        );
    }

    public QueueRankResponse getRank(Long eventProductId, Long userId) {
        validateEventProductExists(eventProductId);

        String queueKey = QueueRedisKey.waitingQueueKey(eventProductId);

        Long rank = redisTemplate.opsForZSet()
                .rank(queueKey, String.valueOf(userId));

        if (rank == null) {
            return new QueueRankResponse(-1L);
        }

        return new QueueRankResponse(rank + 1);
    }

    public QueueStatusResponse getStatus(Long eventProductId, Long userId) {
        validateEventProductExists(eventProductId);

        if (isAvailable(eventProductId, userId)) {
            return new QueueStatusResponse("AVAILABLE");
        }

        Long rank = getRank(eventProductId, userId).getRank();

        if (rank == -1) {
            return new QueueStatusResponse("NOT_ENTERED");
        }

        return new QueueStatusResponse("WAITING");
    }

    public QueueAvailableResponse getAvailable(Long eventProductId, Long userId) {
        validateEventProductExists(eventProductId);

        return new QueueAvailableResponse(
                isAvailable(eventProductId, userId)
        );
    }

    public QueueEstimatedTimeResponse getEstimatedTime(Long eventProductId, Long userId) {
        validateEventProductExists(eventProductId);

        Long rank = getRank(eventProductId, userId).getRank();

        if (rank == -1) {
            return new QueueEstimatedTimeResponse(-1L);
        }

        long averageProcessSeconds = 3;

        return new QueueEstimatedTimeResponse(rank * averageProcessSeconds);
    }

    public QueueCurrentUsersResponse getCurrentUsers(Long eventProductId) {
        validateEventProductExists(eventProductId);

        String queueKey = QueueRedisKey.waitingQueueKey(eventProductId);

        Long size = redisTemplate.opsForZSet().size(queueKey);

        return new QueueCurrentUsersResponse(size);
    }

    public void expire(Long eventProductId) {
        validateQueueAvailable(eventProductId);

        String queueKey = QueueRedisKey.waitingQueueKey(eventProductId);

        Set<String> users = redisTemplate.opsForZSet()
                .range(queueKey, 0, ALLOW_COUNT - 1);

        if (users == null || users.isEmpty()) {
            return;
        }

        for (String userId : users) {
            String availableKey = QueueRedisKey.availableKey(
                    eventProductId,
                    Long.valueOf(userId)
            );

            redisTemplate.opsForValue()
                    .set(
                            availableKey,
                            "true",
                            AVAILABLE_TTL_MINUTES,
                            TimeUnit.MINUTES
                    );
        }

        redisTemplate.opsForZSet()
                .removeRange(queueKey, 0, ALLOW_COUNT - 1);

        publishQueueRealtime(eventProductId, "대기열 앞 순번 사용자가 입장 가능 상태로 변경되었습니다.");
    }

    private void publishQueueRealtime(Long eventProductId, String message) {
        String queueKey = QueueRedisKey.waitingQueueKey(eventProductId);

        Long currentUsers = redisTemplate.opsForZSet().size(queueKey);

        messagingTemplate.convertAndSend(
                "/topic/queue/" + eventProductId,
                new QueueRealtimeResponse(
                        eventProductId,
                        currentUsers == null ? 0L : currentUsers,
                        message
                )
        );
    }

    private boolean isAvailable(Long eventProductId, Long userId) {
        String availableKey = QueueRedisKey.availableKey(eventProductId, userId);

        return Boolean.TRUE.equals(redisTemplate.hasKey(availableKey));
    }

    private void validateEventProductExists(Long eventProductId) {
        EventProductValidationResponse response =
                commerceEventProductClient.validateEventProduct(eventProductId);

        if (response == null || !response.exists()) {
            throw new RuntimeException("존재하지 않는 이벤트 상품입니다.");
        }
    }

    private void validateQueueAvailable(Long eventProductId) {
        EventProductValidationResponse response =
                commerceEventProductClient.validateEventProduct(eventProductId);

        if (response == null || !response.exists()) {
            throw new RuntimeException("존재하지 않는 이벤트 상품입니다.");
        }

        if (!response.queueAvailable()) {
            throw new RuntimeException(
                    "현재 대기열에 진입할 수 없는 이벤트 상품입니다. eventStatus="
                            + response.eventStatus()
                            + ", eventProductStatus="
                            + response.eventProductStatus()
            );
        }
    }

    public void validatePurchaseAccess(Long eventProductId, Long userId) {
        validateQueueAvailable(eventProductId);

        if (!isAvailable(eventProductId, userId)) {
            throw new RuntimeException("아직 구매 가능한 순번이 아닙니다.");
        }
    }

    public void complete(Long eventProductId, Long userId) {
        String availableKey = QueueRedisKey.availableKey(eventProductId, userId);
        redisTemplate.delete(availableKey);
    }

    private void validateQueueIsOpen(Long eventProductId) {
        String status = redisTemplate.opsForValue().get(
                QueueRedisKey.statusKey(eventProductId)
        );

        if ("CLOSED".equals(status)) {
            throw new RuntimeException("관리자가 대기열을 종료했습니다.");
        }
    }
}