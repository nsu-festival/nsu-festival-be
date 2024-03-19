package com.example.nsu_festival.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
