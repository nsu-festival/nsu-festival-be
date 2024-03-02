package com.example.nsu_festival.global.security.exception;

import io.jsonwebtoken.ExpiredJwtException;

public class CustomExpiredJwtException extends RuntimeException {

    //토큰 만료 시간 커스텀 예외 처리
    public CustomExpiredJwtException(String message) {
        super(message);
    }
}
