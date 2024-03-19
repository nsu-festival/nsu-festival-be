package com.example.nsu_festival.domain.notice.service;

import com.example.nsu_festival.domain.notice.dto.NoticeRequestDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;

public interface NoticeService {

    void writeNotice(NoticeRequestDto noticeRequestDto, CustomOAuth2User customOAuth2User);

    void updateNotice(Long noticeId, NoticeRequestDto noticeRequestDto, CustomOAuth2User customOAuth2User);
}
