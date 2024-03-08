package com.example.nsu_festival.domain.festival.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class FestivalProgram {

    @Id
    @NotNull
    @Column(name = "festivalProgram_id")
    private Long festivalProgramId;         // 축제프로그램 id

    private String title;                   // 축제 프로그램 제목

    private LocalTime startTime;            // 프로그램 시작시간

    private LocalTime endTime;              // 프로그램 종료시간

    private int countLike;                  // 좋아요 개수

    @ManyToOne
    @JoinColumn(name = "festivalDate_id")
    private FestivalDate festivalDate;      // 행사 날짜 Entity

    @Builder
    private FestivalProgram(Long festivalProgramId, String title, LocalTime startTime, LocalTime endTime, int countLike, FestivalDate festivalDate){
        this.festivalProgramId = festivalProgramId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.countLike = countLike;
        this.festivalDate = festivalDate;
    }
}
