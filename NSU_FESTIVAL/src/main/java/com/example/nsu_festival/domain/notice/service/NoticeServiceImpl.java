package com.example.nsu_festival.domain.notice.service;

import com.example.nsu_festival.domain.notice.dto.NoticeRequestDto;
import com.example.nsu_festival.domain.notice.entity.Notice;
import com.example.nsu_festival.domain.notice.repository.NoticeRepository;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.domain.user.repository.UserRepository;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 작성 메서드
     */
    @Override
    public void writeNotice(NoticeRequestDto noticeRequestDto, CustomOAuth2User customOAuth2User) {
        if (customOAuth2User != null && isAdmin(customOAuth2User)) {
            String email = customOAuth2User.getEmail();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            Notice notice = Notice.builder()
                    .regDate(LocalDateTime.now())
                    .title(noticeRequestDto.getTitle())
                    .content(noticeRequestDto.getContent())
                    .user(user)
                    .build();
            noticeRepository.save(notice);
        } else {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        }
    }

    /**
     * 관리자인지 판별하는 메서드
     */
    private boolean isAdmin(CustomOAuth2User customOAuth2User){
        String email = customOAuth2User.getEmail();
        if("admin".equals(userRepository.findRoleByEmail(email))){
            return true;
        }
        return false;
    }
}
