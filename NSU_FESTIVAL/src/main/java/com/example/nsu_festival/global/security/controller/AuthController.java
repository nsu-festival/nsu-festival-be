package com.example.nsu_festival.global.security.controller;

import com.example.nsu_festival.domain.user.dto.TokenDto;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.exception.CustomExpiredJwtException;
import com.example.nsu_festival.global.security.exception.JwtException;
import com.example.nsu_festival.global.security.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/logout")
    public ResponseEntity<StatusResponseDto> logout(@RequestHeader(value = "Authorization") String accessToken) {
        //로그아웃 시 AccessToken으로 DB에 저장된 토근 정보 삭제
        try {
            tokenService.removeRefreshToken(accessToken);
            return ResponseEntity.ok(StatusResponseDto.success());
        } catch (IllegalArgumentException i) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(StatusResponseDto.addStatus(401));
        }
    }

    @GetMapping("/reissue/access")
    public ResponseEntity<StatusResponseDto> reissueAccess(@RequestHeader(value = "Authorization") String refreshToken) {
        try {
            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(StatusResponseDto.addStatus(401));
            }
            TokenDto newAccess = tokenService.reissueAccessToken(refreshToken);
            return ResponseEntity.ok().body(StatusResponseDto.success(newAccess));
        } catch (JwtException j) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(StatusResponseDto.addStatus(401));
        }
    }
}
