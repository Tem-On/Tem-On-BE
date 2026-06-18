package com.example.tem_on.monitoring.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionTracker tracker;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        tracker.connect(accessor.getSessionId());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        tracker.disconnect(event.getSessionId());
    }
}