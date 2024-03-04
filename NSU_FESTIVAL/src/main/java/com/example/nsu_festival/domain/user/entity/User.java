package com.example.nsu_festival.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 장소 + 소셜 id(번호)
    private String userName;

    // 소셜 닉네임
    private String nickName;

    private String email;

    private String role;

    @Builder
    public User(Long id, String userName, String nickName, String email, String role) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.email = email;
        this.role = role;
    }

    public void userUpdate(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }
}
