package com.example.nsu_festival.global.security.jwt;

import com.example.nsu_festival.domain.user.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RefreshTokenCleanup {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    //12시간마다 스케줄링
    @Scheduled(fixedRate = 1000L * 60L * 12L)
    @Transactional
    public void deleteExpiredTokens() {
        log.info("만료된 RefreshToken 삭제 시작..");
        //만료된 RefreshToken 삭제
        refreshTokenRepository.deleteExpiredTokens();
        log.info("만료된 RefreshToken 삭제 완료..");
    }
}
