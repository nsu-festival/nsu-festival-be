package com.example.nsu_festival.domain.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TokenDto {

    private String refreshToken;

    private String accessToken;

    private String userEmail;

    @Builder
    public TokenDto(String refreshToken, String accessToken, String userEmail) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.userEmail = userEmail;
    }
}
