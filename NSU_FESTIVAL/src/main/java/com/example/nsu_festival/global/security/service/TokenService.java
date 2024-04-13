package com.example.nsu_festival.global.security.service;


import com.example.nsu_festival.domain.user.dto.TokenDto;
import com.example.nsu_festival.domain.user.entity.RefreshToken;
import com.example.nsu_festival.domain.user.repository.RefreshTokenRepository;
import com.example.nsu_festival.global.security.exception.JwtException;
import com.example.nsu_festival.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public TokenDto reissueAccessToken(String refresh) {

        String token = jwtUtil.resolveToken(refresh);

        //StringUtils.hasText() : null, 빈 문자열, 공백으로만 이루어져 있는 문자열이 아닌 경우만 ture 반환
        if (StringUtils.hasText(token) && jwtUtil.verifyRefreshToken(token)) {
            //토큰에서 사용자 정보(email)을 꺼내 DB에 저장된 RefreshToken을 찾음
            Optional<RefreshToken> findTokens = refreshTokenRepository.findByUserEmail(jwtUtil.getEmail(token));
            RefreshToken resultToken = findTokens.get();
            String email = jwtUtil.getEmail(resultToken.getRefreshToken());
            String role = jwtUtil.getRole(resultToken.getRefreshToken());
            //찾은 RefreshToken의 사용자 정보를 이용해 새로운 AccessToken 발행
            String newAccessToken = jwtUtil.generateAccessToken(email, role);

            return TokenDto.builder()
                    .accessToken(newAccessToken)
                    .build();
        } else {
            throw new JwtException("refreshToken이 만료 또는 잘못된 값입니다.");
        }
    }

    public void removeRefreshToken(String token) {
        //AccessToken으로 DB 데이터를 찾아서 삭제
        RefreshToken findTokens = refreshTokenRepository.findByUserEmail(jwtUtil.getEmail(token)).
                orElseThrow(IllegalArgumentException::new);
        refreshTokenRepository.delete(findTokens);
    }


}
