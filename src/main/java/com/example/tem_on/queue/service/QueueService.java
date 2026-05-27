package com.example.tem_on.queue.service;

import com.example.tem_on.queue.domain.dto.QueueAvailableResponse;
import com.example.tem_on.queue.domain.dto.QueueCurrentUsersResponse;
import com.example.tem_on.queue.domain.dto.QueueEnterResponse;
import com.example.tem_on.queue.domain.dto.QueueEstimatedTimeResponse;
import com.example.tem_on.queue.domain.dto.QueueRankResponse;
import com.example.tem_on.queue.domain.dto.QueueStatusResponse;
import com.example.tem_on.queue.redis.QueueRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int ALLOW_COUNT = 100;

    private static final long AVAILABLE_TTL_MINUTES = 10;

    public QueueEnterResponse enter(
            Long eventProductId,
            Long userId
    ) {

        String queueKey =
                QueueRedisKey.waitingQueueKey(eventProductId);

        Double score =
                redisTemplate.opsForZSet()
                        .score(queueKey, String.valueOf(userId));

        if (score == null) {

            redisTemplate.opsForZSet()
                    .add(
                            queueKey,
                            String.valueOf(userId),
                            System.currentTimeMillis()
                    );
        }

        Long rank =
                redisTemplate.opsForZSet()
                        .rank(queueKey, String.valueOf(userId));

        return new QueueEnterResponse(
                eventProductId,
                userId,
                rank + 1,
                "WAITING"
        );
    }

    public QueueRankResponse getRank(
            Long eventProductId,
            Long userId
    ) {

        String queueKey =
                QueueRedisKey.waitingQueueKey(eventProductId);

        Long rank =
                redisTemplate.opsForZSet()
                        .rank(queueKey, String.valueOf(userId));

        if (rank == null) {
            return new QueueRankResponse(-1L);
        }

        return new QueueRankResponse(rank + 1);
    }

    public QueueStatusResponse getStatus(
            Long eventProductId,
            Long userId
    ) {

        if (isAvailable(eventProductId, userId)) {
            return new QueueStatusResponse("AVAILABLE");
        }

        Long rank =
                getRank(eventProductId, userId).getRank();

        if (rank == -1) {
            return new QueueStatusResponse("NOT_ENTERED");
        }

        return new QueueStatusResponse("WAITING");
    }

    public QueueAvailableResponse getAvailable(
            Long eventProductId,
            Long userId
    ) {

        return new QueueAvailableResponse(
                isAvailable(eventProductId, userId)
        );
    }

    public QueueEstimatedTimeResponse getEstimatedTime(
            Long eventProductId,
            Long userId
    ) {

        Long rank =
                getRank(eventProductId, userId).getRank();

        if (rank == -1) {
            return new QueueEstimatedTimeResponse(-1L);
        }

        long averageProcessSeconds = 3;

        return new QueueEstimatedTimeResponse(
                rank * averageProcessSeconds
        );
    }

    public QueueCurrentUsersResponse getCurrentUsers(
            Long eventProductId
    ) {

        String queueKey =
                QueueRedisKey.waitingQueueKey(eventProductId);

        Long size =
                redisTemplate.opsForZSet()
                        .size(queueKey);

        return new QueueCurrentUsersResponse(size);
    }

    public void expire(Long eventProductId) {

        String queueKey =
                QueueRedisKey.waitingQueueKey(eventProductId);

        Set<String> users =
                redisTemplate.opsForZSet()
                        .range(queueKey, 0, ALLOW_COUNT - 1);

        if (users == null || users.isEmpty()) {
            return;
        }

        for (String userId : users) {

            String availableKey =
                    QueueRedisKey.availableKey(
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
    }

    private boolean isAvailable(
            Long eventProductId,
            Long userId
    ) {

        String availableKey =
                QueueRedisKey.availableKey(
                        eventProductId,
                        userId
                );

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(availableKey)
        );
    }
}