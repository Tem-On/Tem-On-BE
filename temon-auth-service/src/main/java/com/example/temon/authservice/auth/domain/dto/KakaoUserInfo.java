package com.example.temon.authservice.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KakaoUserInfo {

    private Long kakaoId;
    private String email;
    private String nickname;
}
