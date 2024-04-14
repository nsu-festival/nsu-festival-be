package com.example.nsu_festival.global.security.jwt;

import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.exception.CustomExpiredJwtException;
import com.example.nsu_festival.global.security.exception.JwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 다음 필터로 이동
            filterChain.doFilter(request, response);
        } catch (JwtException j) {
            // 응답 상태를 401(인증되지 않음)로 설정
            response.setStatus(401);
            // ContentType을 Json 형태로 설정
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            // 인코딩을 UTF-8로 설정
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), StatusResponseDto.error("토큰 검증 실패."));
        } catch (CustomExpiredJwtException e) {
            // 응답 상태를 401(인증되지 않음)로 설정
            response.setStatus(401);
            // ContentType을 Json 형태로 설정
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            // 인코딩을 UTF-8로 설정
            response.setCharacterEncoding("UTF-8");

            // 토큰 만료 메시지를 응답 본문에 작성
            objectMapper.writeValue(response.getWriter(), StatusResponseDto.error(e.getMessage()));
        }
    }
}
