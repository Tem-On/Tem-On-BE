package com.example.tem_on.auth.ctrl;

import com.example.tem_on.auth.domain.dto.TokenResponse;
import com.example.tem_on.auth.jwt.CustomUserDetails;
import com.example.tem_on.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "카카오 로그인 인증 API")
public class AuthCtrl {

    private final AuthService authService;

    @GetMapping("/oauth/kakao")
    @Operation(
            summary = "카카오 로그인 리다이렉트 ",
            description = "브라우저에서 카카오 로그인 후 JSON 토큰 결과를 바로 확인합니다."
    )
    public ResponseEntity<TokenResponse> kakaoLoginRedirect(
            @RequestParam("code") String code
    ) {
        TokenResponse tokenResponse =
                authService.kakaoLogin(code);

        return ResponseEntity.ok(tokenResponse);
    }


    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "현재 사용자의 Refresh Token을 삭제합니다."
    )
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body("인증 정보가 없습니다.");
        }

        authService.logout(userDetails.getUserId());

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}