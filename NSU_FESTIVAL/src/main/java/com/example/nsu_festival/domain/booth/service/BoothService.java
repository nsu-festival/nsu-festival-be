package com.example.nsu_festival.domain.booth.service;

import com.example.nsu_festival.domain.booth.dto.BoothDetailDto;
import com.example.nsu_festival.domain.booth.dto.AllBoothDto;
import com.example.nsu_festival.domain.booth.entity.Booth;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;

import java.util.List;

public interface BoothService {
    List<AllBoothDto> getAllBooths();
    BoothDetailDto getDetailBooth(Long boothId, CustomOAuth2User customOAuth2User);
//    List<AllBoothDto> getAllFoodTrucks();

    List<AllBoothDto> findTopBooths();
}
