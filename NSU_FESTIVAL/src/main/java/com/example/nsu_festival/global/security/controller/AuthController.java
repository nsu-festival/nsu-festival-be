package com.example.nsu_festival.global.security.controller;

import com.example.nsu_festival.domain.user.dto.TokenDto;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.exception.JwtException;
import com.example.nsu_festival.global.security.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/token/logout")
    public ResponseEntity<StatusResponseDto> logout(@RequestHeader(value = "Authorization") String accessToken) {
        //로그아웃 시 AccessToken으로 DB에 저장된 토근 정보 삭제
        tokenService.removeRefreshToken(accessToken);
        return ResponseEntity.ok(StatusResponseDto.success());
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<StatusResponseDto> refresh(@RequestHeader(value = "RefreshToken") String token) {
        try {
            TokenDto tokenDto = tokenService.reissueAccessToken(token);
            return ResponseEntity.ok(StatusResponseDto.success(tokenDto));
        } catch (JwtException j) {
            return ResponseEntity.ok(StatusResponseDto.addStatus(401));
        }
    }
}
