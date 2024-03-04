package com.example.nsu_festival.global.security.exception;

import io.jsonwebtoken.ExpiredJwtException;

public class JwtException extends RuntimeException{

    //만료 예외를 제외한 나머지 예외 처리
    public JwtException(String message) {
        super(message);
    }
}
