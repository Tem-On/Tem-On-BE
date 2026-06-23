package com.example.temon.authservice.auth.repository;

import com.example.temon.authservice.auth.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
