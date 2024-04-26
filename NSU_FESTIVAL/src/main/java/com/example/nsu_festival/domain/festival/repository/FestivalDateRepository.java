package com.example.nsu_festival.domain.festival.repository;

import com.example.nsu_festival.domain.festival.entity.FestivalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface FestivalDateRepository extends JpaRepository<FestivalDate, Long> {
    @Query("select fd from FestivalDate fd where fd.dDay = :dDay")
    FestivalDate findByDDay(LocalDate dDay);
}
