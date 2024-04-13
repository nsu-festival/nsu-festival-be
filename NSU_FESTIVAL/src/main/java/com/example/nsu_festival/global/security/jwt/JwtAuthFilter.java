package com.example.nsu_festival.global.security.jwt;


import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.exception.CustomExpiredJwtException;
import com.example.nsu_festival.global.security.exception.JwtException;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import com.example.nsu_festival.global.security.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        //'reissue/'이 포함된 uri는 본 클래스 필터를 지나친다.
//        return request.getRequestURI().contains("/reissue/access");
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = null;
        //헤더에서 토큰 값 추출
        authorization= request.getHeader("Authorization");

        // Authorization 헤더 검증
        if (authorization == null) {
            log.info("=====Authorization 헤더 검증 실패...======");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메서드 종료
            return;
        }

        String accessToken = resolveToken(authorization);

        try {
            // AccessToken 만료 시간 검증
            if (!jwtUtil.verifyToken(accessToken)) {
                // AccessToken이 만료되었을 경우 예외 발생
                log.info("=====토큰 검증 실패...=====");
            }
            // AccessToken 검증이 유효한 경우
            if (jwtUtil.verifyToken(accessToken)) {
                log.info("=====토큰 검증 성공...=====");
                // AccessToken payload에 있는 email로 user를 조회
                // 만약 payload가 변조되어 email 조회 시 해당 user가 없다면 예외 발생
                User findUser = userRepository.findByEmail(jwtUtil.getEmail(accessToken))
                        .orElseThrow(IllegalStateException::new);

                // 인증 객체에 저장할 user 정보 생성
                UserDTO userDTO = UserDTO.builder()
                        .name(findUser.getNickName())
                        .email(findUser.getEmail())
                        .role(findUser.getRole())
                        .build();
                CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

                log.info("====={} 인증 정보 생성 시작...======", customOAuth2User.getName());
                // 등록할 인증 객체 생성
                Authentication authentication = getAuthentication(customOAuth2User);
                log.info("====={} 인증 정보 생성 종료...======", customOAuth2User.getName());
                // SecurityContext에 인증 객체 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomExpiredJwtException("Access Token 시간 만료!");
        } catch (Exception e) {
            throw new JwtException("토큰 검증 실패!");
        }


        filterChain.doFilter(request, response);
    }

    //Bearer 타입 파싱
    public String resolveToken(String authorization){
        if(authorization.startsWith(PREFIX)){
            return authorization.substring(PREFIX.length());
        }
        return null;
    }

    // 인증 객체 생성
    public Authentication getAuthentication(CustomOAuth2User customOAuth2User) {
        return new UsernamePasswordAuthenticationToken(customOAuth2User, "", customOAuth2User.getAuthorities());
    }

}
