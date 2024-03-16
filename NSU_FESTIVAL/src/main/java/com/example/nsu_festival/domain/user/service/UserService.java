package com.example.nsu_festival.domain.user.service;

import com.example.nsu_festival.global.security.dto.CustomOAuth2User;

public interface UserService {

    String findUserName(CustomOAuth2User customOAuth2User);
}
