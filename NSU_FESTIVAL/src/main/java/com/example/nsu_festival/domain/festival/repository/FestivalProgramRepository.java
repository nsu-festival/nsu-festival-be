package com.example.nsu_festival.domain.festival.repository;

import com.example.nsu_festival.domain.festival.entity.FestivalProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalProgramRepository extends JpaRepository<FestivalProgram, Long> {
}
