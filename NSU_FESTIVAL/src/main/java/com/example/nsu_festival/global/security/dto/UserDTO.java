package com.example.nsu_festival.global.security.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UserDTO {

    private String role;
    private String name;
    private String username;
    private String email;

    @Builder
    public UserDTO(String role, String name, String username, String email) {
        this.role = role;
        this.name = name;
        this.username = username;
        this.email = email;
    }
}
