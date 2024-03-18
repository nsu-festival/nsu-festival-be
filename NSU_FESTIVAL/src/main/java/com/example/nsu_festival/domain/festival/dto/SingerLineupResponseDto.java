package com.example.nsu_festival.domain.festival.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class SingerLineupResponseDto {
    private Long singerLineupId;        //가수 라인업 id
    private String singer;              //가수 이름
    private int countLike;              //좋아요 개수

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;        //시작 시간

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;          //종료 시간
}
