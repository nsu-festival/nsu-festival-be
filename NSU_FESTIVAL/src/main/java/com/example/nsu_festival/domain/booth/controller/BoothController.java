package com.example.nsu_festival.domain.booth.controller;

import com.example.nsu_festival.domain.booth.dto.*;
import com.example.nsu_festival.domain.booth.service.BoothService;
import com.example.nsu_festival.global.etc.StatusResponseDto;
import com.example.nsu_festival.global.security.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/booths")
public class BoothController {
    private final BoothService boothService;
//    private final AmazonS3 s3Client;

    @GetMapping("")
    public ResponseEntity<StatusResponseDto> getAllBooths(){
        try{
            List<AllBoothDto> booths = boothService.getAllBooths();
            return ResponseEntity.ok(StatusResponseDto.success(booths));
        }catch (Exception e){
            return ResponseEntity.status(400).body(StatusResponseDto.fail(400));
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
    public ResponseEntity<StatusResponseDto> getBoothDetail(@PathVariable Long boothId, @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    , @RequestParam(required = false, defaultValue = "0") int startIndex,  @RequestParam(required = false, defaultValue = "10") int endIndex) {
        try {
            BoothDetailDto boothDetailDto = boothService.getDetailBooth(boothId, customOAuth2User,startIndex,endIndex);

            return ResponseEntity.ok().body(StatusResponseDto.success(boothDetailDto));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(StatusResponseDto.addStatus(404));
        }
    }

//    @GetMapping("/s3")
//    public ResponseEntity<List<String>> getMember() {
//
//        List<String> boothImg = boothService.getBoothImgList("nsufestival");
//
//        return ResponseEntity.ok(boothImg);
//    }


    @GetMapping("/top")
    public ResponseEntity<StatusResponseDto> getTopBooths() {
        try {
            List<TopBoothResponseDto> topBoothList = boothService.findTopBooths();
            return ResponseEntity.ok().body(StatusResponseDto.success(topBoothList));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(StatusResponseDto.addStatus(500));
        }
    }

    //관리자 부스 이름 조회
    @GetMapping("/admin/{boothName}/details")
    public ResponseEntity<StatusResponseDto> getBoothDetail(@PathVariable String boothName){
        try {
            BoothDetailDto boothDetailDto = boothService.getDetailBooth(boothName);
            return ResponseEntity.ok().body(StatusResponseDto.success(boothDetailDto));
        } catch (NoSuchElementException n){
            return ResponseEntity.status(404).body(StatusResponseDto.addStatus(404));
        }
    }


    //관리자 부스 상세 업데이트
    @PatchMapping("/admin/{boothId}/details/update")
    public ResponseEntity<StatusResponseDto> updateBooth(@PathVariable Long boothId,
                                                         @RequestBody BoothDetailsRequestDto requestDto){
        try{
            boothService.updateBoothDetails(boothId, requestDto);
            return ResponseEntity.ok().body(StatusResponseDto.success());
        } catch (NoSuchElementException n){
            return ResponseEntity.status(404).body(StatusResponseDto.addStatus(404));
        } catch (RuntimeException r){
            return ResponseEntity.status(500).body(StatusResponseDto.addStatus(500));
        }
    }

}
