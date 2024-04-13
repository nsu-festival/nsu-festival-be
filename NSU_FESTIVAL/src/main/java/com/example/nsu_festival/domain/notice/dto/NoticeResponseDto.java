package com.example.nsu_festival.domain.notice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeResponseDto {
    private Long noticeId;
    private String title;
    private LocalDateTime creatAt;
    private String content;
    private boolean isAdmin;
}
