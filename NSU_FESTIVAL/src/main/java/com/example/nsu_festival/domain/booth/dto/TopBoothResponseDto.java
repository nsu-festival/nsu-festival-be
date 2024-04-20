package com.example.nsu_festival.domain.booth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopBoothResponseDto {

    private String title;

    @Builder
    public TopBoothResponseDto(String title){
        this.title = title;
    }
}
