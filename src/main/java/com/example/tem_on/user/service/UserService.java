package com.example.tem_on.user.service;

import com.example.tem_on.user.domain.dto.UserRequest;
import com.example.tem_on.user.domain.dto.UserResponse;
import com.example.tem_on.user.domain.entity.UserEntity;
import com.example.tem_on.user.repository.UserRepository;

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
    public UserResponse updateUserProfile(Long userId, UserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));
        
        user.updateProfile(request.getPassword(), request.getNickname());
        
        return new UserResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));
        userRepository.delete(user);
    }
}