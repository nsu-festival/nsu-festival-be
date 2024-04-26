package com.example.nsu_festival.domain.festival.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class FestivalProgram {

    @Id
    @Column(name = "festivalProgram_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long festivalProgramId;         // 축제프로그램 id

    private String title;                   // 축제 프로그램 제목

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;            // 프로그램 시작시간

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;              // 프로그램 종료시간

    private int countLike;                  // 좋아요 개수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festivalDate_id")
    private FestivalDate festivalDate;      // 행사 날짜 Entity

    @Builder
    public FestivalProgram(Long festivalProgramId, String title, String startTime, String endTime, int countLike, FestivalDate festivalDate){
        this.festivalProgramId = festivalProgramId;
        this.title = title;
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
        this.countLike = countLike;
        this.festivalDate = festivalDate;
    }

    public void updateCountLike(int countLike){
        this.countLike = countLike;
    }
}
