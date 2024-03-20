package com.example.nsu_festival.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
