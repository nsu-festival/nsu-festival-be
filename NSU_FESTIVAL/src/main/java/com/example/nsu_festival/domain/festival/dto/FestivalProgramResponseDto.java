package com.example.nsu_festival.domain.festival.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@Builder
public class FestivalProgramResponseDto {
    private Long festivalProgramId;     //축제프로그램 id
    private String title;               //축제 프로그램 이름
    private int countLike;              //축제 프로그램 좋아요 개수

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;             //시작시간
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;               //종료시간
}
