package com.example.temon.authservice.user.repository;


import com.example.temon.authservice.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByKakaoId(Long kakaoId);
}