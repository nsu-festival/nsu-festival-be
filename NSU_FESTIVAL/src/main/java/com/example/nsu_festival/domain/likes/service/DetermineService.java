package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.likes.dto.UserLikeDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;

import java.util.List;

public interface DetermineService {
    boolean determineUser(String contentType, CustomOAuth2User customOAuth2User);

    boolean determineContents(String contentType, Long contentId);

    List<UserLikeDto> findUserLike (String contentType, CustomOAuth2User customOAuth2User, int day);

    void createUserLike(String contentType, CustomOAuth2User customOAuth2User);

    List<UserLikeDto> convertToDto(String contentType, Object likeContentList);
}
