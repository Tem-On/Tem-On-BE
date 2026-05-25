package com.example.tem_on.user.ctrl;

import com.example.tem_on.user.domain.dto.UserRequest;
import com.example.tem_on.user.domain.dto.UserResponse;
import com.example.tem_on.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
public class UserCtrl {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 반환합니다.")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        UserResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/me")
    @Operation(summary = "내 프로필 수정", description = "비밀번호, 닉네임 등의 내 정보를 수정합니다.")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        UserResponse updatedProfile = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제(탈퇴)합니다.")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "회원 단건 조회", description = "지정한 사용자 고유 ID로 프로필 정보를 반환합니다.")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        UserResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
}