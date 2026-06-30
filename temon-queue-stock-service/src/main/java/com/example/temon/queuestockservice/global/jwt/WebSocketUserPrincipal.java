package com.example.temon.queuestockservice.global.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Getter
@RequiredArgsConstructor
public class WebSocketUserPrincipal implements Principal {

    private final Long userId;
    private final String role;

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}