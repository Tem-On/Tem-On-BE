package com.example.temon.queuestockservice.monitoring.websocket;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionTracker {

    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    public void connect(String sessionId) {
        if (sessionId != null) {
            sessions.add(sessionId);
        }
    }

    public void disconnect(String sessionId) {
        if (sessionId != null) {
            sessions.remove(sessionId);
        }
    }

    public int getConnectedUsers() {
        return sessions.size();
    }
}