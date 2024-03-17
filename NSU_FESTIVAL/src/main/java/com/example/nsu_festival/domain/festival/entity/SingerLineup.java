package com.example.nsu_festival.domain.festival.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor
public class SingerLineup {
    @Id
    @Column(name = "singerLineup_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long singerLineupId;            // 가수 라인업 id

    private String singer;                  // 가수 이름

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;            // 프로그램 시작시간

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;              // 프로그램 종료시간

    private int countLike;                  // 좋아요 개수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festivalDate_id")
    private FestivalDate festivalDate;

    @Builder
    private SingerLineup(Long singerLineupId, String singer, LocalTime startTime, LocalTime endTime, int countLike, FestivalDate festivalDate){
        this.singerLineupId = singerLineupId;
        this.singer = singer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.countLike = countLike;
        this.festivalDate = festivalDate;
    }

    public void updateCountLike(int countLike){
        this.countLike = countLike;
    }
}
