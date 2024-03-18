package com.example.nsu_festival.domain.festival.service;

import com.example.nsu_festival.domain.festival.entity.SingerLineup;

import java.time.LocalDate;
import java.util.List;

public interface SingerLineupService {
    List<SingerLineup> findSingerLineupList(LocalDate dDay);
}
