package com.example.nsu_festival.domain.festival.service;

import com.example.nsu_festival.domain.festival.dto.FestivalProgramResponseDto;
import com.example.nsu_festival.domain.festival.entity.FestivalProgram;

import java.time.LocalDate;
import java.util.List;

public interface FestivalProgramService {
    List<FestivalProgramResponseDto> findFestivalProgramList(LocalDate dDay);
    List<FestivalProgramResponseDto> convertToDto(List<FestivalProgram> festivalProgramList);
    boolean isCorrectDate(LocalDate dDay);
}
