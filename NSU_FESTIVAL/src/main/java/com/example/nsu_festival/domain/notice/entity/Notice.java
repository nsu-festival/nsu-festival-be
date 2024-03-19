package com.example.nsu_festival.domain.notice.entity;

import com.example.nsu_festival.domain.notice.dto.NoticeRequestDto;
import com.example.nsu_festival.domain.user.entity.User;
import com.example.nsu_festival.global.baseEntity.BaseTimeRegDateEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Notice extends BaseTimeRegDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    Notice(Long noticeId, String title, String content, LocalDateTime creatAt, User user){
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.creatAt = creatAt;
        this.user = user;
    }

    public void updateNotice(NoticeRequestDto noticeRequestDto, LocalDateTime creatAt, User user){
        this.title = noticeRequestDto.getTitle();
        this.content = noticeRequestDto.getContent();
        this.creatAt = creatAt;
        this.user = user;
    }
}
