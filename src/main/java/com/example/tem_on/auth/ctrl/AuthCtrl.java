package com.example.tem_on.auth.ctrl;

import com.example.tem_on.auth.domain.dto.KakaoLoginRequest;
import com.example.tem_on.auth.domain.dto.TokenResponse;
import com.example.tem_on.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "카카오 로그인 인증 API")
public class AuthCtrl {

    private final AuthService authService;

    @PostMapping("/kakao")
    @Operation(
            summary = "카카오 로그인",
            description = "카카오 인가 코드를 통해 로그인 후 JWT를 발급합니다."
    )
    public ResponseEntity<TokenResponse> kakaoLogin(
            @RequestBody KakaoLoginRequest request
    ) {
        TokenResponse tokenResponse =
                authService.kakaoLogin(request.getCode());

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "현재 사용자의 Refresh Token을 삭제합니다."
    )
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body("인증 정보가 없습니다.");
        }
        Long userId =
                Long.parseLong(userDetails.getUsername());
        authService.logout(userId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}