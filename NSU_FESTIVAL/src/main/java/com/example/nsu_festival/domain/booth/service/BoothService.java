package com.example.nsu_festival.domain.booth.service;

import com.example.nsu_festival.domain.booth.dto.BoothDetailDto;
import com.example.nsu_festival.domain.booth.dto.BoothDto;

import java.util.List;

public interface BoothService {
    List<BoothDto> getAllBooths();
    BoothDetailDto getDetailBooth(Long boothId);
}
