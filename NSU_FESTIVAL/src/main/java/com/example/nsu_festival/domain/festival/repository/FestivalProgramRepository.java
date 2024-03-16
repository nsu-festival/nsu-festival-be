package com.example.nsu_festival.domain.festival.repository;

import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FestivalProgramRepository extends JpaRepository<FestivalProgram, Long> {
    List<FestivalProgram> findAllByFestivalDate(FestivalDate festivalDate);
}
