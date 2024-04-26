package com.example.nsu_festival.domain.festival.service;

import com.example.nsu_festival.domain.festival.dto.SingerLineupResponseDto;
import com.example.nsu_festival.domain.festival.entity.SingerLineup;

import java.time.LocalDate;
import java.util.List;

public interface SingerLineupService {
    List<SingerLineupResponseDto> findSingerLineupList(LocalDate dDay);
    List<SingerLineupResponseDto> convertToDto(List<SingerLineup> singerLineupList);
    boolean isCorrectDate(LocalDate dDay);
    void initializeData();                                                                  // 각 초기 데이터 삽입

}
