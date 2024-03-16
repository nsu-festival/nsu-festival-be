package com.example.nsu_festival.domain.festival.repository;

import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SingerLineupRepository extends JpaRepository<SingerLineup, Long> {
    List<SingerLineup> findAllByFestivalDate(FestivalDate festivalDate);
}
