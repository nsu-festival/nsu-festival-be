package com.example.nsu_festival.domain.booth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopBoothResponseDto {

    private Long boothId;
    private String title;

    @Builder
    public TopBoothResponseDto(Long boothId, String title){
        this.boothId = boothId;
        this.title = title;
    }
}
