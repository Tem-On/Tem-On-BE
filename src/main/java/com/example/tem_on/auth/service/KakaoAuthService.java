package com.example.tem_on.auth.service;

import com.example.tem_on.auth.domain.dto.KakaoUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestClient restClient = RestClient.create();

    public String getKakaoAccessToken(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        JsonNode response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
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

        String email = null;
        String nickname = "카카오사용자";

        if (kakaoAccount != null) {

            if (kakaoAccount.has("email")) {
                email = kakaoAccount.get("email").asText();
            }

            JsonNode profile = kakaoAccount.get("profile");

            if (profile != null && profile.has("nickname")) {
                nickname = profile.get("nickname").asText();
            }
        }

        return KakaoUserInfo.builder()
                .kakaoId(kakaoId)
                .email(email)
                .nickname(nickname)
                .build();
    }
}