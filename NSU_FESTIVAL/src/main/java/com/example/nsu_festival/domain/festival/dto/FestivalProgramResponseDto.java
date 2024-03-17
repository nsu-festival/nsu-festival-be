package com.example.nsu_festival.domain.festival.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class FestivalProgramResponseDto {
    private Long festivalProgramId;     //축제프로그램 id
    private String title;               //축제 프로그램 이름
    private int countLike;              //축제 프로그램 좋아요 개수
    private LocalTime startTime;             //시작시간
    private LocalTime endTime;               //종료시간
}
