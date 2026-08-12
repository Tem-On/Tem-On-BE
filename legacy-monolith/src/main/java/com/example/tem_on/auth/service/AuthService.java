package com.example.tem_on.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tem_on.auth.domain.dto.KakaoUserInfo;
import com.example.tem_on.auth.domain.dto.TokenResponse;
import com.example.tem_on.auth.domain.entity.RefreshToken;
import com.example.tem_on.auth.jwt.JwtProvider;
import com.example.tem_on.auth.repository.RefreshTokenRepository;
import com.example.tem_on.user.domain.entity.Role;
import com.example.tem_on.user.domain.entity.UserEntity;
import com.example.tem_on.user.domain.entity.UserStatus;
import com.example.tem_on.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final KakaoAuthService kakaoAuthService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public TokenResponse kakaoLogin(String code) {

        String kakaoAccessToken = kakaoAuthService.getKakaoAccessToken(code);

        KakaoUserInfo kakaoUserInfo =
                kakaoAuthService.getKakaoUserInfo(kakaoAccessToken);

        UserEntity user = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId())
                .orElseGet(() -> userRepository.save(
                        UserEntity.builder()
                                .kakaoId(kakaoUserInfo.getKakaoId())
                                .email(kakaoUserInfo.getEmail())
                                .nickname(kakaoUserInfo.getNickname())
                                .role(Role.USER)
                                .status(UserStatus.ACTIVE)
                                .build()
                ));

        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getRole().name()
        );

        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        saveRefreshToken(user.getId(), refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse reissue(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new IllegalArgumentException("저장된 Refresh Token이 없습니다.")
                );

        UserEntity user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다.")
                );

        String newAccessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getRole().name()
        );

        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

        savedToken.updateToken(
                newRefreshToken,
                LocalDateTime.now()
                        .plusSeconds(jwtProvider.getRefreshExpiration() / 1000)
        );

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private void saveRefreshToken(Long userId, String refreshToken) {

        LocalDateTime expiredAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshExpiration() / 1000);

        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .map(existingToken -> {
                    existingToken.updateToken(refreshToken, expiredAt);
                    return existingToken;
                })
                .orElseGet(() -> RefreshToken.builder()
                        .userId(userId)
                        .token(refreshToken)
                        .expiredAt(expiredAt)
                        .build());

        refreshTokenRepository.save(token);
    }
}