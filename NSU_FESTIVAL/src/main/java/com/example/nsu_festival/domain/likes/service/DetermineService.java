package com.example.nsu_festival.domain.likes.service;

import com.example.nsu_festival.domain.likes.dto.UserLikeDto;
import com.example.nsu_festival.domain.likes.entity.ContentType;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;

import java.time.LocalDate;
import java.util.List;

public interface DetermineService {
    boolean determineUser(ContentType contentType, CustomOAuth2User customOAuth2User);

    boolean determineContents(ContentType contentType, Long contentId);

    List<UserLikeDto> findUserLike (ContentType contentType, CustomOAuth2User customOAuth2User, LocalDate day);

    void createUserLike(ContentType contentType, CustomOAuth2User customOAuth2User);

    List<UserLikeDto> convertToDto(ContentType contentType, Object likeContentList);

    List<UserLikeDto> findNoUserLike(ContentType contentType, LocalDate dDay);
}
