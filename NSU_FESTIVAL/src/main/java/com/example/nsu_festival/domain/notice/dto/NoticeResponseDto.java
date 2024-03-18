package com.example.nsu_festival.domain.notice.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeResponseDto {
    private Long noticeId;
    private String title;
    private LocalDate cheatAt;
    private String content;
}
