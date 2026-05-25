package com.example.tem_on.user.domain.dto;

import com.example.tem_on.user.domain.entity.UserEntity;
import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String role;
    private final String status;

    public UserResponse(UserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.status = user.getStatus();
    }
}