package com.example.nsu_festival.domain.booth.service;

import com.example.nsu_festival.domain.booth.dto.BoothDetailDto;
import com.example.nsu_festival.domain.booth.dto.BoothDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;

import java.util.List;

public interface BoothService {
    List<BoothDto> getAllBooths();
    BoothDetailDto getDetailBooth(Long boothId, CustomOAuth2User customOAuth2User);
    List<BoothDto> getAllFoodTrucks();

}
