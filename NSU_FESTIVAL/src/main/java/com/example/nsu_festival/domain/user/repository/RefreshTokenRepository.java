package com.example.nsu_festival.domain.user.repository;

import com.example.nsu_festival.domain.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserEmail(String userEmail);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiration < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
