package com.example.temon.authservice.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;

    private Long userId;
    private String nickname;
    private String email;
    private String role;
}
