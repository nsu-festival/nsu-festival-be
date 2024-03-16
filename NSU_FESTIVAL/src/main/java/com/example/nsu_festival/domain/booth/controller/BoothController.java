package com.example.nsu_festival.domain.booth.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.nsu_festival.domain.booth.dto.*;
import com.example.nsu_festival.domain.booth.service.BoothService;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/booths")
public class BoothController {
    private final BoothService boothService;
    private final AmazonS3 s3Client;

    @GetMapping("")
    public ResponseEntity<StatusResponseDto> getAllBooths(){
        try{
            List<AllBoothDto> booths = boothService.getAllBooths();
            return ResponseEntity.ok(StatusResponseDto.success(booths));
        }catch (Exception e){
            return ResponseEntity.ok(StatusResponseDto.fail(400));
        }
    }

//    @GetMapping("/foodTruck")
//
//    public ResponseEntity<StatusResponseDto> getAllFoodTrucks(){
//        try{
//            List<AllBoothDto> allBoothDtos = boothService.getAllFoodTrucks();
//            return ResponseEntity.ok(StatusResponseDto.success(allBoothDtos));
//
//        }catch (Exception e){
//            return ResponseEntity.ok(StatusResponseDto.fail(400));
//        }
//    }


    @GetMapping("/{boothId}/details")
    public ResponseEntity<StatusResponseDto> getBoothDetail(@PathVariable Long boothId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        try {
            BoothDetailDto boothDetailDto = boothService.getDetailBooth(boothId, customOAuth2User);

            return ResponseEntity.ok().body(StatusResponseDto.success(boothDetailDto));
        } catch (Exception e) {
            return ResponseEntity.ok().body(StatusResponseDto.addStatus(404));
        }
    }

//    @GetMapping("/s3/{boothId}")
//    public ResponseEntity<StatusResponseDto> getMember(@PathVariable("boothId") Long boothId) {
//
//        URL url = s3Client.getUrl("nsufestival", Long.toString(boothId));
//        String urltext = ""+url+".png";
//
//        return ResponseEntity.ok(StatusResponseDto.success(urltext));
//    }


}
