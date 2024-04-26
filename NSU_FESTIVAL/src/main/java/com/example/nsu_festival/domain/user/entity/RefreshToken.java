package com.example.nsu_festival.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    private String userEmail;

    private Date expiration;

    public RefreshToken(String refreshToken, Date expiration, String userEmail) {
        this.refreshToken = refreshToken;
        this.expiration = expiration;
        this.userEmail = userEmail;
    }

    public void updateRefreshToken(String refreshToken, Date expiration) {
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }
}
