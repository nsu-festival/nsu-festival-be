package com.example.nsu_festival.domain.notice.service;

import com.example.nsu_festival.domain.notice.dto.NoticeRequestDto;
import com.example.nsu_festival.domain.notice.dto.NoticeResponseDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;

import java.util.List;

public interface NoticeService {

    List<NoticeResponseDto> findAllNoticeList();

    NoticeResponseDto findNoticeDetail(Long noticeId);

    void writeNotice(NoticeRequestDto noticeRequestDto, CustomOAuth2User customOAuth2User);

    void updateNotice(Long noticeId, NoticeRequestDto noticeRequestDto, CustomOAuth2User customOAuth2User);

    void deleteNotice(Long noticeId, CustomOAuth2User customOAuth2User);
}
