package com.example.nsu_festival.domain.festival.repository;

import com.example.nsu_festival.domain.festival.entity.SingerLineup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingerLineupRepository extends JpaRepository<SingerLineup, Long> {
}
