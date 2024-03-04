package com.example.nsu_festival.global.security.handler;

import com.example.nsu_festival.domain.user.entity.GeneratedToken;
import com.example.nsu_festival.global.security.jwt.JwtUtil;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //인증된 사용자 정보 가져오기
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = customOAuth2User.getEmail();

        //인증된 사용자의 권한(authorities)에서 첫 번째 권한을 가져와서 역할(role) 변수에 저장합니다.
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(IllegalAccessError::new);
        //Access/Refresh Token 생성
        GeneratedToken generatedToken = jwtUtil.generateToken(email, role);

        //각 토큰을 쿠키에 저장한 후 응답에 담아 넘긴다.
        response.addCookie(createCookie("AccessToken", generatedToken.getAccessToken()));
        response.addCookie(createCookie("RefreshToken", generatedToken.getRefreshToken()));

    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        //쿠키 지속 시간
        cookie.setMaxAge(60*60*60);
        //쿠키 보일 위치는 모든 전역
        cookie.setPath("/");
        //자바스크립트로 쿠키 못 가져가도록 설정
        cookie.setHttpOnly(true);

        return cookie;
    }
}
