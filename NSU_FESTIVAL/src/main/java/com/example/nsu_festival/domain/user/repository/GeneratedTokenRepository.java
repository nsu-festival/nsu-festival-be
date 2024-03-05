package com.example.nsu_festival.domain.user.repository;

import com.example.nsu_festival.domain.user.entity.GeneratedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneratedTokenRepository extends JpaRepository<GeneratedToken, Long> {

    Optional<GeneratedToken> findByUserEmail(String userEmail);

    Optional<GeneratedToken> findByAccessToken(String accessToken);
}
