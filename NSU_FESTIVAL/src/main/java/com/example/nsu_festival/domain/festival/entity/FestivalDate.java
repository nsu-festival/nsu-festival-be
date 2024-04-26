package com.example.nsu_festival.domain.festival.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class FestivalDate {
    @Id
    @Column(name = "festivalDate_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long festivalDateId;            // 행사날짜 id

    @Column(unique = true, name = "dDay")
    private LocalDate dDay;                 // 행사 날짜

    @OneToMany(mappedBy = "festivalDate")
    private List<FestivalProgram> festivalProgramList;

    @OneToMany(mappedBy = "festivalDate")
    private List<SingerLineup> singerLineupList;

    @Builder

    public FestivalDate(Long festivalDateId, String dDay){
        this.festivalDateId = festivalDateId;
        this.dDay = LocalDate.parse(dDay);
    }
}
