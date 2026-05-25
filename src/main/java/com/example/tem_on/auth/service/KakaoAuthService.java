package com.example.tem_on.auth.service;

import com.example.tem_on.auth.domain.dto.KakaoUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestClient restClient = RestClient.create();

    public String getKakaoAccessToken(String code) {
        JsonNode response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=authorization_code"
                        + "&client_id=" + clientId
                        + "&redirect_uri=" + redirectUri
                        + "&code=" + code)
                .retrieve()
                .body(JsonNode.class);

        return response.get("access_token").asText();
    }

    public KakaoUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        JsonNode response = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(JsonNode.class);

        Long kakaoId = response.get("id").asLong();

        JsonNode kakaoAccount = response.get("kakao_account");

        String email = kakaoAccount.has("email")
                ? kakaoAccount.get("email").asText()
                : null;

        JsonNode profile = kakaoAccount.get("profile");

        String nickname = profile.has("nickname")
                ? profile.get("nickname").asText()
                : "카카오사용자";

        return KakaoUserInfo.builder()
                .kakaoId(kakaoId)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
