package com.example.nsu_festival.domain.festival.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class FestivalDate {
    @Id
    @NotNull
    @Column(name = "festivalDate_id")
    private Long festivalDateId;            // 행사날짜 id

    private LocalDate dDay;                 // 행사 날짜

    @OneToMany(mappedBy = "festivalDate")
    private List<FestivalProgram> festivalProgramList;

    @OneToMany(mappedBy = "festivalDate")
    private List<SingerLineup> singerLineupList;

    @Builder

    private FestivalDate(Long festivalDateId, LocalDate dDay){
        this.festivalDateId = festivalDateId;
        this.dDay = dDay;
    }
}
