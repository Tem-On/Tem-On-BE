package com.example.temon.authservice.user.service;

import com.example.temon.authservice.user.domain.dto.UserResponse;
import com.example.temon.authservice.user.domain.entity.UserEntity;
import com.example.temon.authservice.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateUserProfile(Long userId, String nickname) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));

        user.updateProfile(nickname);
        return new UserResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));
        userRepository.delete(user);
    }

    @Transactional
    public void updateFcmToken(Long userId, String fcmToken) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        
        user.updateFcmToken(fcmToken); 
    }
}