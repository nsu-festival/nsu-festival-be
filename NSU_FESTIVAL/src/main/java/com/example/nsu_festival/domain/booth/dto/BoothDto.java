package com.example.nsu_festival.domain.booth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoothDto {
    private Long boothId;
    private String title;
    private String content;
    private Long countLike;
    private String area;
}
