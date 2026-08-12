package com.example.tem_on.global.config;

import com.example.tem_on.auth.jwt.CustomUserDetails;
import com.example.tem_on.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");

        if (authorization == null || authorization.isBlank()) {
            throw new MessagingException("WebSocket 인증 토큰이 없습니다.");
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new MessagingException("잘못된 WebSocket 토큰 형식입니다.");
        }

        String token = authorization.substring(7);

        if (!jwtProvider.validateToken(token)) {
            throw new MessagingException("유효하지 않은 WebSocket 토큰입니다.");
        }

        Long userId = jwtProvider.getUserId(token);
        String role = jwtProvider.getRole(token);

        CustomUserDetails userDetails = new CustomUserDetails(userId, role);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        accessor.setUser(authentication);

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes != null) {
            sessionAttributes.put("user", authentication);
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (destination == null) {
            return;
        }

        if (destination.equals("/topic/events")) {
            return;
        }

        Principal user = accessor.getUser();

        if (user == null && accessor.getSessionAttributes() != null) {
            user = (Principal) accessor.getSessionAttributes().get("user");
        }

        if (user == null) {
            throw new MessagingException("로그인이 필요한 WebSocket topic입니다.");
        }

        if (destination.startsWith("/topic/admin")) {
            boolean isAdmin =
                    ((UsernamePasswordAuthenticationToken) user)
                            .getAuthorities()
                            .stream()
                            .anyMatch(auth ->
                                    auth.getAuthority().equals("ROLE_ADMIN")
                            );

            if (!isAdmin) {
                throw new MessagingException("ADMIN만 구독할 수 있는 topic입니다.");
            }
        }
    }
}