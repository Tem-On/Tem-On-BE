package com.example.tem_on.queue.redis;

public class QueueRedisKey {

    private QueueRedisKey() {
    }

    public static String waitingQueueKey(Long eventProductId) {
        return "queue:waiting:event-product:" + eventProductId;
    }

    public static String availableKey(Long eventProductId, Long userId) {
        return "queue:available:event-product:"
                + eventProductId
                + ":user:"
                + userId;
    }

    public static String availableKeyPattern(Long eventProductId) {
        return "queue:available:event-product:"
                + eventProductId
                + ":user:*";
    }

    public static String statusKey(Long eventProductId) {
        return "queue:status:event-product:" + eventProductId;
    }
}