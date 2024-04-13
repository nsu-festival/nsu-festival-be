package com.example.nsu_festival.global.security.jwt;

import com.example.nsu_festival.domain.user.dto.TokenDto;
import com.example.nsu_festival.domain.user.entity.RefreshToken;
import com.example.nsu_festival.domain.user.repository.RefreshTokenRepository;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import io.jsonwebtoken.Jwts;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class JwtUtil {

    private static final String PREFIX = "Bearer ";
    private SecretKey secretKey;
    private final Long ACCESS_TOKEN_EXPIRE_LENGTH = 1000L * 60L * 30L;      // 만료일 30분
    private final Long REFRESH_TOKEN_EXPIRE_LENGTH = 1000L * 60L * 60L * 10L;       // 만료일 10시간
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        // secret 키를 UTF8로 인코딩하고, HS256 암호화 설정
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public TokenDto generateToken(String email, String role) {

        //과거 refreshToken 모두 삭제
        refreshTokenRepository.deleteAllByUserEmail(email);

        // RefreshToken 발급
        String refreshToken = generateRefreshToken(email, role);
        // AccessToken 발급
        String accessToken = generateAccessToken(email, role);
        log.info("-------Refresh 토큰 저장 시작...-------");
        Date expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_LENGTH);
        refreshTokenRepository.save(new RefreshToken(refreshToken, expiration, email));
        log.info("-------Refresh 토큰 저장 완료...-------");

        return new TokenDto(refreshToken, accessToken, email);
    }

    public String generateAccessToken(String email, String role) {
        log.info("=====Access Token 생성..=====");
        // 현재 시간과 날짜를 가져옴
        Date now = new Date();

        return Jwts.builder()
                // 알고리즘과 타입 정의
                .header()
                    .add("alg", "HS256")
                    .add("typ", "JWT")
                .and()
                // Payload 구성하는 속성 정의
                .claim("email", email)
                .claim("roel", role)
                // 발행일자
                .issuedAt(now)
                // 만료일자
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_LENGTH))
                // 서명 알고리즘과 비밀 키를 사용해 토큰 서명
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String email, String role) {
        log.info("=====Refresh Token 생성...=====");;
        // 현재 시간과 날짜를 가져옴
        Date now = new Date();

        return Jwts.builder()
                // 알고리즘과 타입 정의
                .header()
                    .add("alg", "HS256")
                    .add("typ", "JWT")
                .and()
                // Payload 구성하는 속성 정의
                .claim("email", email)
                .claim("roel", role)
                // 발행일자
                .issuedAt(now)
                // 만료일자
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_LENGTH))
                // 서명 알고리즘과 비밀 키를 사용해 토큰 서명
                .signWith(secretKey)
                .compact();
    }

    // 토큰 만료 시간 검증 메서드
    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token);
            return claims.getPayload().getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    //RefreshToken 검증
    public boolean verifyRefreshToken(String token) {
        //1차 토큰 검증
        if (!verifyToken(token)) {
            return false;
        }

        //유저 정보가 일치하는 DB 토큰 찾기
        Optional<RefreshToken> findTokens = refreshTokenRepository.findByUserEmail(getEmail(token));

        //해당 유저의 토큰이 DB에 저장되어 있고, 요청한 RefreshToken과 DB RefreshToken이 일치하는지 검증
        return findTokens.isPresent() && token.equals(findTokens.get().getRefreshToken());

    }


    public String getEmail(String token) {
        // JWT 파싱 후 이메일 획득
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }


    public String getRole(String token) {
        // JWT 파싱 후 역할 획득
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    //Bearer 타입 파싱
    public String resolveToken(String authorization){
        if(authorization.startsWith(PREFIX)){
            return authorization.substring(PREFIX.length());
        }
        return null;
    }

    public boolean isRefreshToken(String email){
        return refreshTokenRepository.existsByUserEmail(email);
    }

//    @Transactional
//    public void removePreviousToken(String email){
//        refreshTokenRepository.deleteByUserEmail(email);
//    }

}
